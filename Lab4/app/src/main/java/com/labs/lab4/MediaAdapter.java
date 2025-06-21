package com.labs.lab4;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.VH> {
    private final Context ctx;
    private final List<MediaEntry> items;
    private final MediaDao dao;
    private final ExecutorService executor;
    private MediaPlayer player;
    private int playingPos = -1;
    private VH playingHolder = null;
    private static final String TAG = "MediaAdapter";

    public MediaAdapter(Context c, List<MediaEntry> lst, MediaDao d, ExecutorService exec) {
        ctx = c;
        items = lst;
        dao = d;
        executor = exec;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        View vew = LayoutInflater.from(ctx).inflate(R.layout.item_media, p, false);
        return new VH(vew);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        h.bind(items.get(pos));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void releasePlayer() {
        stopAudio();
    }

    class VH extends RecyclerView.ViewHolder {
        ImageButton btnPlay, btnMore;
        SeekBar seek;
        TextView tvName, tvTime;
        Runnable updateSeekBarRunnable;
        MediaEntry entry;

        VH(View v) {
            super(v);
            btnPlay = v.findViewById(R.id.btnPlay);
            btnMore = v.findViewById(R.id.btnMore);
            seek = v.findViewById(R.id.seekBar);
            tvName = v.findViewById(R.id.tvName);
            tvTime = v.findViewById(R.id.tvTime);

            btnPlay.setOnClickListener(vv -> {
                if (entry.isVideo) {
                    Intent i = new Intent(ctx, VideoActivity.class);
                    i.putExtra("path", entry.path);
                    ctx.startActivity(i);
                } else {
                    handleAudio();
                }
            });

            seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar sb, int p, boolean u) {
                    if (u && player != null) player.seekTo(p);
                }
                public void onStartTrackingTouch(SeekBar sb) {}
                public void onStopTrackingTouch(SeekBar sb) {}
            });

            btnMore.setOnClickListener(vv -> {
                PopupMenu pm = new PopupMenu(ctx, vv);
                pm.getMenuInflater().inflate(R.menu.media_item_menu, pm.getMenu());
                pm.setOnMenuItemClickListener(mi -> {
                    if (mi.getItemId() == R.id.action_delete) {
                        int currentPos = getAbsoluteAdapterPosition();
                        if (currentPos != RecyclerView.NO_POSITION) {
                            deleteItem(currentPos);
                        }
                        return true;
                    }
                    return false;
                });
                pm.show();
            });
        }

        void bind(MediaEntry e) {
            entry = e;
            tvName.setText(entry.name);
            seek.setVisibility(entry.isVideo ? View.GONE : View.VISIBLE);
            tvTime.setVisibility(entry.isVideo ? View.GONE : View.VISIBLE);

            if (getAbsoluteAdapterPosition() == playingPos && player != null) {
                btnPlay.setImageResource(player.isPlaying() ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play);
                updateSeekBar();
            } else {
                btnPlay.setImageResource(android.R.drawable.ic_media_play);
                seek.setProgress(0);
                tvTime.setText("00:00");
            }
        }

        void deleteItem(int position) {
            MediaEntry entryToDelete = items.get(position);

            if (position == playingPos) {
                stopAudio();
            }

            executor.execute(() -> {
                dao.delete(entryToDelete);
                new File(entryToDelete.path).delete();
            });
            items.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, items.size());
        }

        void handleAudio() {
            int currentPos = getAbsoluteAdapterPosition();
            if (currentPos == RecyclerView.NO_POSITION) return;

            if (playingPos == currentPos) {
                if (player.isPlaying()) {
                    player.pause();
                    btnPlay.setImageResource(android.R.drawable.ic_media_play);
                    itemView.removeCallbacks(updateSeekBarRunnable);
                } else {
                    player.start();
                    btnPlay.setImageResource(android.R.drawable.ic_media_pause);
                    updateSeekBar();
                }
            } else {
                stopAudio();
                startAudio(currentPos);
            }
        }

        void startAudio(int pos) {
            player = new MediaPlayer();
            player.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "MediaPlayer Error: what=" + what + ", extra=" + extra);
                Toast.makeText(ctx, "Не вдалося відтворити файл", Toast.LENGTH_SHORT).show();
                stopAudio();
                return true;
            });

            try {
                player.setDataSource(entry.path);
                player.setOnPreparedListener(mp -> {
                    int oldPlayingPos = playingPos;
                    playingPos = pos;
                    playingHolder = this;
                    notifyItemChanged(oldPlayingPos); // Оновити старий елемент

                    seek.setMax(mp.getDuration());
                    mp.start();
                    btnPlay.setImageResource(android.R.drawable.ic_media_pause);
                    updateSeekBar();
                });
                player.setOnCompletionListener(mp -> stopAudio());
                player.prepareAsync();
            } catch (IOException e) {
                Log.e(TAG, "Error setting data source", e);
                Toast.makeText(ctx, "Невірний файл або шлях", Toast.LENGTH_SHORT).show();
                stopAudio();
            }
        }

        void updateSeekBar() {
            if (player != null && player.isPlaying()) {
                seek.setProgress(player.getCurrentPosition());
                int current = player.getCurrentPosition() / 1000;
                tvTime.setText(String.format("%02d:%02d", current / 60, current % 60));

                updateSeekBarRunnable = this::updateSeekBar;
                itemView.postDelayed(updateSeekBarRunnable, 500);
            }
        }
    }

    private void stopAudio() {
        if (player != null) {
            if (player.isPlaying()) player.stop();
            player.release();
            player = null;
        }

        int previouslyPlaying = playingPos;
        playingPos = -1;
        playingHolder = null;

        if (previouslyPlaying != -1) {
            notifyItemChanged(previouslyPlaying);
        }
    }
}