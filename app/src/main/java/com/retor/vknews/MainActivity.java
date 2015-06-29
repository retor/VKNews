package com.retor.vknews;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.retor.vklib.Const;
import com.retor.vklib.DialogsBuilder;
import com.retor.vklib.auth.Authorizator;
import com.retor.vklib.auth.IAuth;
import com.retor.vklib.auth.IAuthListener;
import com.retor.vknews.newsfragment.NewsFragment;
import com.vk.sdk.VKUIHelper;

public class MainActivity extends AppCompatActivity implements IAuthListener {
    private IAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VKUIHelper.onCreate(this);
//        this.auth = new Authorizator(this, this);
        initToolbar();
//        auth.authorization();
    }

    private boolean isNetworkConnected() {
        NetworkInfo ni = ((ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }

    private void showNetworkAlert() {
        android.app.AlertDialog al = DialogsBuilder.createAlert(this, "No internet connection");
        al.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog.dismiss();
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        });
        al.show();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_logout) {
                    logout();
                    return true;
                }
                return false;
            }
        });
    }

    private void logout() {
        if (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() > 0) {
            getSupportFragmentManager().beginTransaction().detach(getSupportFragmentManager().findFragmentByTag(Const.FRAGMENT)).commit();
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag(Const.FRAGMENT)).commit();
        }
        if (auth != null) {
            auth.logout();
            auth.authWithLogin();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        VKUIHelper.onResume(this);
        if (isNetworkConnected()) {
            if (auth != null)
                auth.authorization();
            else {
                auth = new Authorizator(this, this);
                auth.authorization();
            }
        } else {
            showNetworkAlert();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void createFragment() {
        getSupportFragmentManager().beginTransaction().add(R.id.container, new NewsFragment(), Const.FRAGMENT).commit();
    }

    @Override
    public void onLogin(int userId) {
        createFragment();
    }

    @Override
    public void onError(String message) {
        if (message != null && (message.isEmpty() || message.equals("")))
            DialogsBuilder.createAlert(this, message).show();
        else
            DialogsBuilder.createAlert(this, "Access denied(Please auth)").show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
