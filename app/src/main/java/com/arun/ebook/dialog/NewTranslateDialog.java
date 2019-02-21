package com.arun.ebook.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.arun.ebook.R;
import com.arun.ebook.bean.TranslateData;
import com.arun.ebook.listener.DialogListener;

public class NewTranslateDialog extends DialogFragment {
    private DialogListener listener;
    private MediaPlayer mediaPlayer;

    public void setListener(DialogListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity(), R.style.DialogBottomStyle);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View transView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_new_translate, null);
        dialog.setContentView(transView);
        dialog.setCanceledOnTouchOutside(true);
        getActivity().getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 设置宽度为屏宽、位置靠近屏幕顶部
        Window window = dialog.getWindow();
        if (window != null) {
            window.setDimAmount(0f);
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(wlp);
        }
        TranslateData bean = null;
        int bgColor = Color.parseColor("#F2F1ED");
        int textColor = Color.parseColor("#15140F");
        if (getArguments() != null) {
            if (getArguments().containsKey("TranslateData")) {
                bean = (TranslateData) getArguments().getSerializable("TranslateData");
            }
            if (getArguments().containsKey("bgColor")) {
                bgColor = getArguments().getInt("bgColor");
            }
            if (getArguments().containsKey("textColor")) {
                textColor = getArguments().getInt("textColor");
            }
        }
        setData(transView, bean);
        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (listener != null) {
            listener.onDismiss();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (listener != null) {
            listener.onDismiss();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    private void setData(View itemView, TranslateData bean) {
        TextView mWordTranslate = itemView.findViewById(R.id.text_word);
        TextView mCloseImg = itemView.findViewById(R.id.btn_close);
        TextView mVoiceTranslate = itemView.findViewById(R.id.translate_voice);
        ImageView mVoiceImg = itemView.findViewById(R.id.img_voice);
        TextView mDetailTranslate = itemView.findViewById(R.id.translate_detail);

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
            Uri uri = Uri.parse(bean.speak_url);
            mediaPlayer = MediaPlayer.create(getActivity(), uri);
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
    }
}
