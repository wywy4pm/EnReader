package com.arun.ebook.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arun.ebook.R;
import com.arun.ebook.bean.TranslateBean;
import com.arun.ebook.listener.DialogListener;

/**
 * Created by Administrator on 2018/4/13.
 */

public class TranslateDialog extends DialogFragment {

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
        final Dialog dialog = new Dialog(getActivity(), R.style.DialogTopStyle);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_translate, null);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        // 设置宽度为屏宽、位置靠近屏幕顶部
        Window window = dialog.getWindow();
        if (window != null) {
            window.setDimAmount(0f);
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(wlp);
        }
        if (getArguments() != null && getArguments().containsKey("TranslateBean")) {
            TranslateBean bean = (TranslateBean) getArguments().getSerializable("TranslateBean");
            setData(view, bean);
        }
        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (listener != null) {
            listener.onDismiss();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (listener != null) {
            listener.onDismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    private void setData(View itemView, final TranslateBean bean) {
        TextView mWordTranslate = itemView.findViewById(R.id.translate_word);
        ImageView mCloseImg = itemView.findViewById(R.id.img_close);
        RelativeLayout mCenterTranslate = itemView.findViewById(R.id.translate_center);
        TextView mVoiceTranslate = itemView.findViewById(R.id.translate_voice);
        ImageView mVoiceImg = itemView.findViewById(R.id.img_voice);
        TextView mDetailTranslate = itemView.findViewById(R.id.translate_detail);
        if (bean != null) {
            mWordTranslate.setText(bean.word);
            if (bean.voice != null) {
                mVoiceTranslate.setVisibility(View.VISIBLE);
                mVoiceTranslate.setText(String.valueOf("[" + bean.voice.us_phonetic + "]"));
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
            mCenterTranslate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
                        Uri uri = Uri.parse(bean.speak_url);
                        mediaPlayer = MediaPlayer.create(getActivity(), uri);
                        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                            @Override
                            public boolean onError(MediaPlayer mp, int what, int extra) {
                                Log.d("TAG", "MediaPlayer onError = " + what);
                                return false;
                            }
                        });
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mediaPlayer.stop();
                                //mediaPlayer.release();
                            }
                        });
                        mediaPlayer.start();
                    }
                }
            });

        }
    }

}
