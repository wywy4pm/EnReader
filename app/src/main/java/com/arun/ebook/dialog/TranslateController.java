package com.arun.ebook.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.arun.ebook.R;
import com.arun.ebook.bean.TranslateData;
import com.arun.ebook.listener.DialogListener;

public class TranslateController implements DialogInterface.OnDismissListener, DialogInterface.OnCancelListener {
    private Context context;
    private TranslateData translateData;
    private Dialog dialog;
    private View transView;
    private DialogListener listener;
    private MediaPlayer mediaPlayer;
    private Window window;
    private int readBg;
    private int textColor;
    private TextView mWordTranslate, mVoiceTranslate, mDetailTranslate;

    public TranslateController(Context context) {
        this.context = context;
    }

    public void setTranslateData(TranslateData translateData) {
        this.translateData = translateData;
    }

    public void setListener(DialogListener listener) {
        this.listener = listener;
    }

    public void setReadBg(int readBg) {
        this.readBg = readBg;
        if (window != null && readBg != 0) {
            window.getDecorView().setBackgroundColor(readBg);
        }
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        if (textColor != 0) {
            if (mWordTranslate != null) {
                mWordTranslate.setTextColor(textColor);
            }
            if (mVoiceTranslate != null) {
                mVoiceTranslate.setTextColor(textColor);
            }
            if (mDetailTranslate != null) {
                mDetailTranslate.setTextColor(textColor);
            }
        }
    }

    public void showDialog() {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(context).create();
            transView = LayoutInflater.from(context).inflate(R.layout.layout_new_translate, null);
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
            dialog.setContentView(transView);
            window = dialog.getWindow();
            if (window != null) {
                window.getDecorView().setPadding(0, 0, 0, 0);
                window.getDecorView().setBackgroundColor(context.getResources().getColor(R.color.black));
                window.setGravity(Gravity.BOTTOM);
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
                wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(wlp);
            }
            dialog.setOnDismissListener(this);
            dialog.setOnCancelListener(this);
        } else {
            dialog.show();
        }
        if (transView != null && translateData != null) {
            setData(transView, translateData);
        }
    }

    /*public void cancel() {
        if (dialog != null) {
            dialog.cancel();
        }
        if (listener != null) {
            listener.onDismiss();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }*/

    public boolean isShowing() {
        if (dialog != null) {
            return dialog.isShowing();
        }
        return false;
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    private void setData(View itemView, TranslateData bean) {
        mWordTranslate = itemView.findViewById(R.id.text_word);
        TextView mCloseImg = itemView.findViewById(R.id.btn_close);
        mVoiceTranslate = itemView.findViewById(R.id.translate_voice);
        ImageView mVoiceImg = itemView.findViewById(R.id.img_voice);
        mDetailTranslate = itemView.findViewById(R.id.translate_detail);

        if (bean != null) {
            mWordTranslate.setText(bean.keyword);
            if (bean.voice != null) {
                mVoiceTranslate.setVisibility(View.VISIBLE);
                mVoiceTranslate.setText(String.valueOf("英 [" + bean.voice.uk_phonetic + "]" + " 美 [" + bean.voice.us_phonetic + "]"));
            } else {
                mVoiceTranslate.setVisibility(View.GONE);
            }
            if (bean.explains != null && bean.explains.size() > 0) {
                StringBuilder showExplain = new StringBuilder();
                for (int i = 0; i < bean.explains.size(); i++) {
                    showExplain.append(bean.explains.get(i));
                    if (i < bean.explains.size() - 1) {//最后一行不需要换行
                        showExplain.append("\n");
                    }
                }
                mDetailTranslate.setText(showExplain.toString());
            }

            mCloseImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            startReadTranslation(bean);
            setVoiceClick(mWordTranslate, bean);
            setVoiceClick(mVoiceImg, bean);
            setVoiceClick(mVoiceTranslate, bean);
        }
    }

    private void setVoiceClick(View view, final TranslateData bean) {
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startReadTranslation(bean);
                }
            });
        }
    }

    private void startReadTranslation(TranslateData bean) {
        if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
            final Uri uri = Uri.parse(bean.speak_url);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mediaPlayer = MediaPlayer.create(context, uri);
                    if (mediaPlayer != null) {
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                Log.d("TAG", "TransMediaPlayer onPrepared");
                            }
                        });
                        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                            @Override
                            public boolean onError(MediaPlayer mp, int what, int extra) {
                                Log.d("TAG", "TransMediaPlayer onError = " + what);
                                return false;
                            }
                        });
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                Log.d("TAG", "TransMediaPlayer onCompletion");
                                mediaPlayer.stop();
                            }
                        });
                        mediaPlayer.start();
                    }
                }
            }).start();

        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (listener != null) {
            listener.onDismiss();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (listener != null) {
            listener.onDismiss();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
