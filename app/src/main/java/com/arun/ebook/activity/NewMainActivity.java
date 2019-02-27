package com.arun.ebook.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.arun.ebook.R;
import com.arun.ebook.adapter.MainAdapter;
import com.arun.ebook.bean.AppBean;
import com.arun.ebook.bean.BookItemBean;
import com.arun.ebook.common.Constant;
import com.arun.ebook.event.UidEvent;
import com.arun.ebook.fragment.AnswerFragment;
import com.arun.ebook.fragment.InteractFragment;
import com.arun.ebook.fragment.MainFragment;
import com.arun.ebook.fragment.MessageFragment;
import com.arun.ebook.fragment.MineFragment;
import com.arun.ebook.helper.AppHelper;
import com.arun.ebook.helper.PermissionsChecker;
import com.arun.ebook.presenter.MainPresenter;
import com.arun.ebook.utils.SharedPreferencesUtils;
import com.arun.ebook.view.CommonView4;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class NewMainActivity extends BaseActivity implements CommonView4<List<BookItemBean>>, View.OnClickListener {
    // 所需的全部权限
    public static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_PHONE_STATE
    };
    private PermissionsChecker mPermissionsChecker; // 权限检测器
    private static final int REQUEST_CODE = 0; // 请求码

    private MainPresenter mainPresenter;
    private ViewPager viewPager;
    private List<Fragment> fragmentList = new ArrayList<>();
    //private ImageView main_tab, answer_tab, interact_tab, message_tab, mine_tab;
    //private int[] tabIds = new int[]{R.id.main_tab, R.id.answer_tab, R.id.interact_tab, R.id.message_tab, R.id.mine_tab};
    private int[] tabIds = new int[]{R.id.main_tab, R.id.mine_tab};
    private List<ImageView> tabViews = new ArrayList<>();

    public static void jumpToMain(Context context) {
        Intent intent = new Intent(context, NewMainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        mPermissionsChecker = new PermissionsChecker(this);
        setContentView(R.layout.activity_new_main);
        initView();
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        } else {
            initData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFullScreen();
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
            if (resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
                finish();
            } else if (resultCode == PermissionsActivity.PERMISSIONS_GRANTED) {
                AppHelper.getInstance().setAppConfig(this);
                initData();
            }
        }
    }

    private void initView() {
        viewPager = findViewById(R.id.viewPager);
        for (int i = 0; i < tabIds.length; i++) {
            ImageView imageView = findViewById(tabIds[i]);
            imageView.setOnClickListener(this);
            tabViews.add(imageView);
        }

        /*main_tab = findViewById(R.id.main_tab);
        answer_tab = findViewById(R.id.answer_tab);
        interact_tab = findViewById(R.id.interact_tab);
        message_tab = findViewById(R.id.message_tab);
        mine_tab = findViewById(R.id.mine_tab);
        main_tab.setOnClickListener(this);
        answer_tab.setOnClickListener(this);
        interact_tab.setOnClickListener(this);
        message_tab.setOnClickListener(this);
        mine_tab.setOnClickListener(this);*/

        MainFragment mainFragment = MainFragment.newInstance();
        /*AnswerFragment answerFragment = new AnswerFragment();
        InteractFragment interactFragment = new InteractFragment();
        MessageFragment messageFragment = new MessageFragment();*/
        MineFragment mineFragment = new MineFragment();
        fragmentList.add(mainFragment);
        /*fragmentList.add(answerFragment);
        fragmentList.add(interactFragment);
        fragmentList.add(messageFragment);*/
        fragmentList.add(mineFragment);
        viewPager.setAdapter(new MainAdapter(getSupportFragmentManager(), fragmentList));
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setSelectTab(position);
            }
        });
        viewPager.setCurrentItem(0);
        if (tabViews != null && tabViews.size() > 0) {
            tabViews.get(0).setSelected(true);
        }

    }

    private void initData() {
        mainPresenter = new MainPresenter();
        mainPresenter.attachView(this);
        mainPresenter.userRegister();
    }

    @Override
    public void refresh(List<BookItemBean> data) {
    }

    @Override
    public void refreshMore(List<BookItemBean> data) {
    }

    @Override
    public void refresh(int type, Object data) {
        if (type == MainPresenter.TYPE_REGISTER) {
            if (data instanceof AppBean) {
                AppBean bean = (AppBean) data;
                if (!TextUtils.isEmpty((bean.uid))) {
                    SharedPreferencesUtils.saveUid(this, bean.uid);
                    EventBus.getDefault().postSticky(new UidEvent(bean.uid));
                    Log.d("TAG", "register success");
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_tab:
                setSelectTab(Constant.TAB_INDEX_MAIN);
                break;
            /*case R.id.answer_tab:
                setSelectTab(Constant.TAB_INDEX_ANSWER);
                break;
            case R.id.interact_tab:
                setSelectTab(Constant.TAB_INDEX_INTERACT);
                break;
            case R.id.message_tab:
                setSelectTab(Constant.TAB_INDEX_MESSAGE);
                break;*/
            case R.id.mine_tab:
                setSelectTab(Constant.TAB_INDEX_MINE);
                break;
        }
    }

    private void setSelectTab(int index) {
        viewPager.setCurrentItem(index);
        setSelect(index);
    }

    private void setSelect(int selectPos) {
        if (tabViews != null && tabViews.size() > 0) {
            for (int i = 0; i < tabViews.size(); i++) {
                ImageView imageView = tabViews.get(i);
                if (selectPos == i) {
                    imageView.setSelected(true);
                } else {
                    imageView.setSelected(false);
                }
            }
        }
    }
}
