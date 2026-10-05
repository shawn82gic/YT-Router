package yt.router.android;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import java.util.ArrayList;

public class RouterActivity extends Activity {

    
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        Intent in = getIntent();
        Uri uri = in.getData();
        String type = in.getType();
        boolean done = false;
        if (uri != null) {
            String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase();
            String host = uri.getHost() == null ? "" : uri.getHost().toLowerCase();
            boolean web = scheme.equals("http") || scheme.equals("https");
            boolean yt = web && (host.equals("youtu.be") || host.equals("youtube.com")
                    || (host.endsWith(".youtube.com") && !host.equals("music.youtube.com")));
            if (yt) { String y = Util.ytChoice(this); if (y != null) done = open(uri, null, y); }
            if (!done) {
                for (String pkg : Util.browserOrder(this)) {
                    if (open(uri, type, pkg)) { done = true; break; }
                }
            }
            if (!done) done = chooser(uri, type);
        }
        if (!done) Toast.makeText(this, "YT Router: no app can open this link", Toast.LENGTH_LONG).show();
        finish();
    }

    private Intent build(Uri uri, String type) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        if (type != null) i.setDataAndType(uri, type); else i.setData(uri);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return i;
    }

    private boolean open(Uri uri, String type, String pkg) {
        try {
            Intent i = build(uri, type);
            i.setPackage(pkg);
            startActivity(i);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean chooser(Uri uri, String type) {
        try {
            Intent i = build(uri, type);
            Intent c = Intent.createChooser(i, "Open with");
            if (Build.VERSION.SDK_INT >= 24) {
                ArrayList<ComponentName> ex = new ArrayList<ComponentName>();
                ex.add(new ComponentName(this, RouterActivity.class));
                c.putExtra("android.intent.extra.EXCLUDE_COMPONENTS", ex);
            }
            c.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(c);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
