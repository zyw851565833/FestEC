package com.dianbin.fastec.example;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.widget.Toast;

import com.dianbin.latte.activities.ProxyActivity;
import com.dianbin.latte.app.Latte;
import com.dianbin.latte.delegates.LatteDelegate;
import com.dianbin.latte.ec.launcher.LauncherDelegate;
import com.dianbin.latte.ec.main.EcBottomDelegate;
import com.dianbin.latte.ec.sign.ISignListener;
import com.dianbin.latte.ec.sign.SignInDelegate;
import com.dianbin.latte.ui.launcher.ILauncherListener;
import com.dianbin.latte.ui.launcher.OnLauncherFinishTag;


public class ExampleActivity extends ProxyActivity implements ISignListener, ILauncherListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        Latte.getConfigurator().withActivity(this);
    }

    @Override
    public LatteDelegate setRootDelegate() {
        return new LauncherDelegate();
    }

    @Override
    public void onSignInSuccess() {
        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSignUpSuccess() {
        Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLauncherFinish(OnLauncherFinishTag tag) {
        switch (tag) {
            case SIGNED:
//                Toast.makeText(this, "启动结束，用户登录了", Toast.LENGTH_LONG).show();
                startWithPop(new EcBottomDelegate());
                break;
            case NOT_SIGNED:
                Toast.makeText(this, "启动结束，没登录", Toast.LENGTH_LONG).show();
                startWithPop(new SignInDelegate());
                break;
            default:
                break;
        }

    }
}
