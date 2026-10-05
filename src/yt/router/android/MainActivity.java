package yt.router.android;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    static final int BG = 0xFF0D1014;
    static final int CARD = 0xFF171B22;
    static final int CARD_SEL = 0xFF1C2130;
    static final int ACCENT = 0xFF7C6CFF;
    static final int GREEN = 0xFF3DDC84;
    static final int AMBER = 0xFFFFB347;
    static final int TXT = 0xFFF2F4F8;
    static final int SUB = 0xFF8A93A3;

    LinearLayout root;
    float density;
    boolean zh;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        density = getResources().getDisplayMetrics().density;
        zh = Locale.getDefault().getLanguage().equals("zh");
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        ScrollView sv = new ScrollView(this);
        sv.setBackgroundColor(BG);
        sv.setFillViewport(true);
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(20), dp(18), dp(28));
        sv.addView(root);
        setContentView(sv);
    }

    @Override
    protected void onResume() {
        super.onResume();
        build();
    }

    private int dp(int v) { return (int) (v * density + 0.5f); }

    private String s(String en, String cn) { return zh ? cn : en; }

    private GradientDrawable shape(int fill, int radiusDp, int stroke, int strokeDp) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(fill);
        g.setCornerRadius(dp(radiusDp));
        if (strokeDp > 0) g.setStroke(dp(strokeDp), stroke);
        return g;
    }

    private GradientDrawable oval(int fill, int stroke, int strokeDp) {
        GradientDrawable g = new GradientDrawable();
        g.setShape(GradientDrawable.OVAL);
        g.setColor(fill);
        if (strokeDp > 0) g.setStroke(dp(strokeDp), stroke);
        return g;
    }

    private TextView text(String t, int sp, int color, boolean bold) {
        TextView tv = new TextView(this);
        tv.setText(t);
        tv.setTextSize(sp);
        tv.setTextColor(color);
        if (bold) tv.setTypeface(Typeface.DEFAULT_BOLD);
        return tv;
    }

    private LinearLayout.LayoutParams lp(int w, int h, int l, int t, int r, int b) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(w, h);
        p.setMargins(dp(l), dp(t), dp(r), dp(b));
        return p;
    }

    private boolean isDefaultBrowser() {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(Util.WEB_TEST));
        ResolveInfo r = getPackageManager().resolveActivity(i, PackageManager.MATCH_DEFAULT_ONLY);
        return r != null && r.activityInfo.packageName.equals(getPackageName());
    }

    private void openDefaultSettings() {
        try {
            startActivity(new Intent("android.settings.MANAGE_DEFAULT_APPS_SETTINGS"));
        } catch (Exception e) {
            startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
        }
    }

    private void statusCard() {
        boolean ok = isDefaultBrowser();
        int c = ok ? GREEN : AMBER;
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18), dp(16), dp(18), dp(16));
        card.setBackground(shape(CARD, 18, c, 1));

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        View dot = new View(this);
        dot.setBackground(oval(c, 0, 0));
        row.addView(dot, lp(dp(10), dp(10), 0, 0, 10, 0));
        row.addView(text(ok ? s("Default browser is active", "已设为默认浏览器")
                : s("Not the default browser yet", "还没有设为默认浏览器"), 16, TXT, true));
        card.addView(row);

        card.addView(text(ok ? s("Links are being routed.", "链接正在按你的设置转发。")
                : s("Set YT Router as your default Browser app so links reach it.",
                    "请把 YT Router 设为默认浏览器应用，链接才会转发过来。"), 13, SUB, false),
                lp(-1, -2, 0, 6, 0, 0));

        TextView btn = text(ok ? s("Change in Settings", "去设置里修改") : s("Set as default", "设为默认"),
                14, ok ? TXT : Color.WHITE, true);
        btn.setGravity(Gravity.CENTER);
        btn.setPadding(dp(16), dp(11), dp(16), dp(11));
        btn.setBackground(ok ? shape(0xFF262B36, 24, 0, 0) : shape(ACCENT, 24, 0, 0));
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { openDefaultSettings(); }
        });
        card.addView(btn, lp(-1, -2, 0, 14, 0, 0));
        root.addView(card, lp(-1, -2, 0, 0, 0, 0));
    }

    private void kofiCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setPadding(dp(18), dp(14), dp(18), dp(14));
        card.setBackground(shape(CARD, 18, 0, 0));
        card.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        col.addView(text(s("Support Development", "支持开发"), 15, TXT, true));
        col.addView(text(s("If you find this app useful, consider buying me a coffee!",
                "如果这个应用对你有用，请考虑请我喝杯咖啡！"), 13, SUB, false),
                lp(-1, -2, 0, 4, 0, 0));

        card.addView(col, new LinearLayout.LayoutParams(0, -2, 1f));

        TextView btn = text("☕ Ko-fi", 14, Color.WHITE, true);
        btn.setGravity(Gravity.CENTER);
        btn.setPadding(dp(20), dp(10), dp(20), dp(10));
        btn.setBackground(shape(0xFFFF5E5B, 24, 0, 0));
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://ko-fi.com/shawn82")));
                } catch (Exception e) {
                    // ignore
                }
            }
        });
        card.addView(btn, lp(-2, -2, 12, 0, 0, 0));

        root.addView(card, lp(-1, -2, 0, 14, 0, 0));
    }

    private void openOfficialLinkSettings() {
        Uri pkg = Uri.parse("package:" + Util.OFFICIAL);
        try {
            startActivity(new Intent("android.settings.APP_OPEN_BY_DEFAULT_SETTINGS", pkg));
        } catch (Exception e) {
            try {
                startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, pkg));
            } catch (Exception e2) {
                openDefaultSettings();
            }
        }
    }

    private void linkCard() {
        if (!Util.officialInstalled(this) || Util.OFFICIAL.equals(Util.ytChoice(this))) return;
        Boolean allowed = Util.officialLinksAllowed(this);
        if (allowed != null && !allowed) {
            TextView ok = text("✓ " + s("Official YouTube is not grabbing links", "官方 YouTube 没有抢链接"),
                    13, GREEN, false);
            root.addView(ok, lp(-1, -2, 4, 12, 0, 0));
            return;
        }
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18), dp(14), dp(18), dp(14));
        card.setBackground(shape(CARD, 18, AMBER, 1));
        card.addView(text(allowed == null
                ? s("If official YouTube still opens", "如果还是打开官方 YouTube")
                : s("Official YouTube is grabbing links", "官方 YouTube 正在抢链接"), 15, TXT, true));
        card.addView(text(s("Turn off \"Open supported links\" for official YouTube. The app stays installed and enabled.",
                "把官方 YouTube 的「打开支持的链接」关掉即可，不需要停用或卸载它。"), 13, SUB, false),
                lp(-1, -2, 0, 6, 0, 0));
        TextView btn = text(s("Open YouTube link settings", "打开 YouTube 链接设置"), 14, Color.WHITE, true);
        btn.setGravity(Gravity.CENTER);
        btn.setPadding(dp(16), dp(11), dp(16), dp(11));
        btn.setBackground(shape(0xFF3A3220, 24, AMBER, 1));
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { openOfficialLinkSettings(); }
        });
        card.addView(btn, lp(-1, -2, 0, 12, 0, 0));
        root.addView(card, lp(-1, -2, 0, 14, 0, 0));
    }

    private void section(String title, String sub, String badge, int badgeColor) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(text(title, 18, TXT, true));
        if (badge != null) {
            TextView b = text(badge, 11, badgeColor, true);
            b.setPadding(dp(8), dp(2), dp(8), dp(2));
            b.setBackground(shape(0x22FFFFFF, 10, badgeColor, 1));
            row.addView(b, lp(-2, -2, 10, 0, 0, 0));
        }
        root.addView(row, lp(-1, -2, 4, 26, 0, 0));
        root.addView(text(sub, 13, SUB, false), lp(-1, -2, 4, 2, 0, 8));
    }

    private void item(Drawable icon, String placeholder, String title, String sub,
                      boolean selected, View.OnClickListener l) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(14), dp(12), dp(14), dp(12));
        row.setBackground(shape(selected ? CARD_SEL : CARD, 16, ACCENT, selected ? 2 : 0));
        row.setOnClickListener(l);

        if (icon != null) {
            ImageView iv = new ImageView(this);
            iv.setImageDrawable(icon);
            row.addView(iv, lp(dp(42), dp(42), 0, 0, 14, 0));
        } else {
            TextView ph = text(placeholder, 18, Color.WHITE, true);
            ph.setGravity(Gravity.CENTER);
            ph.setBackground(oval(ACCENT, 0, 0));
            row.addView(ph, lp(dp(42), dp(42), 0, 0, 14, 0));
        }

        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        col.addView(text(title, 15, TXT, true));
        TextView sb = text(sub, 12, SUB, false);
        sb.setSingleLine(true);
        col.addView(sb);
        row.addView(col, new LinearLayout.LayoutParams(0, -2, 1f));

        TextView mark = text(selected ? "✓" : "", 13, Color.WHITE, true);
        mark.setGravity(Gravity.CENTER);
        mark.setBackground(selected ? oval(ACCENT, 0, 0) : oval(0, 0xFF3A4150, 2));
        row.addView(mark, lp(dp(24), dp(24), 10, 0, 0, 0));

        root.addView(row, lp(-1, -2, 0, 0, 0, 8));
    }

    private void build() {
        root.removeAllViews();
        final PackageManager pm = getPackageManager();

        LinearLayout head = new LinearLayout(this);
        head.setOrientation(LinearLayout.HORIZONTAL);
        head.setGravity(Gravity.CENTER_VERTICAL);
        int logoId = getResources().getIdentifier("logo", "drawable", getPackageName());
        if (logoId != 0) {
            ImageView lg = new ImageView(this);
            lg.setImageResource(logoId);
            head.addView(lg, lp(dp(52), dp(52), 0, 0, 14, 0));
        }
        LinearLayout ht = new LinearLayout(this);
        ht.setOrientation(LinearLayout.VERTICAL);
        ht.addView(text("YT Router", 26, TXT, true));
        ht.addView(text(s("Send each link to the app you prefer", "让每个链接用你喜欢的 app 打开"), 13, SUB, false));
        head.addView(ht);
        root.addView(head, lp(-1, -2, 0, 0, 0, 16));

        statusCard();
        kofiCard();

        // YouTube
        boolean off = Util.officialInstalled(this);
        section("YouTube", s("Opens youtube.com and youtu.be links", "用于打开 youtube.com 和 youtu.be 链接"),
                off ? s("Official YouTube detected", "已检测到官方 YouTube")
                    : s("Official YouTube not installed", "未安装官方 YouTube"),
                off ? GREEN : SUB);
        List<ResolveInfo> yt = Util.ytApps(this);
        String ytSel = Util.ytChoice(this);
        linkCard();
        for (ResolveInfo r : yt) {
            final String p = r.activityInfo.packageName;
            String sub = p.equals(Util.OFFICIAL) ? p + "  ·  " + s("Official", "官方") : p;
            item(r.loadIcon(pm), null, r.loadLabel(pm).toString(), sub, p.equals(ytSel),
                    new View.OnClickListener() {
                        public void onClick(View v) {
                            Util.prefs(MainActivity.this).edit().putString("yt", p).apply();
                            build();
                        }
                    });
        }
        if (yt.isEmpty()) {
            root.addView(text(s("No YouTube app found. Links will open in your browser.",
                    "没有找到 YouTube app，链接会用浏览器打开。"), 13, AMBER, false), lp(-1, -2, 4, 0, 0, 0));
        }

        // Browser
        section(s("Browser", "浏览器"), s("Opens all other links and .html files", "用于打开其他链接和 .html 文件"), null, 0);
        String brSel = Util.prefs(this).getString("browser", "auto");
        item(null, "A", s("Auto", "自动"), s("Chrome first, then any other browser", "优先 Chrome，其次其他浏览器"),
                brSel.equals("auto"), new View.OnClickListener() {
                    public void onClick(View v) {
                        Util.prefs(MainActivity.this).edit().putString("browser", "auto").apply();
                        build();
                    }
                });
        for (ResolveInfo r : Util.handlers(this, Util.WEB_TEST)) {
            final String p = r.activityInfo.packageName;
            if (p.equals(Util.MORPHE)) continue;
            item(r.loadIcon(pm), null, r.loadLabel(pm).toString(), p, p.equals(brSel),
                    new View.OnClickListener() {
                        public void onClick(View v) {
                            Util.prefs(MainActivity.this).edit().putString("browser", p).apply();
                            build();
                        }
                    });
        }

        TextView foot = text("YT Router v1.0", 12, 0xFF5B6475, false);
        foot.setGravity(Gravity.CENTER);
        root.addView(foot, lp(-1, -2, 0, 24, 0, 0));
    }
}
