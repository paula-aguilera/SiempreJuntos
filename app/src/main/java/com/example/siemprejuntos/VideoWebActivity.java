package com.example.siemprejuntos;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;

/**
 * Muestra videos y páginas (YouTube, jw.org) DENTRO de la aplicación, para que la barra
 * "Volver / Inicio" de abajo siempre esté disponible y la persona nunca quede fuera.
 */
public class VideoWebActivity extends BaseActivity {

    public static final String EXTRA_URL = "url";

    /** Sitios permitidos dentro de la aplicación (además del sitio del enlace inicial). */
    private static final String[] PERMITIDOS = {
            "youtube.com", "youtu.be", "youtube-nocookie.com", "google.com", "gstatic.com",
            "ytimg.com", "googlevideo.com", "googleusercontent.com", "jw.org", "jw-cdn.org"
    };

    private WebView web;
    private ProgressBar progreso;
    private FrameLayout raiz;
    private View vistaCompleta;
    private WebChromeClient.CustomViewCallback callbackCompleta;
    private String hostInicial = "";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        raiz = findViewById(R.id.flWeb);
        web = findViewById(R.id.webVideo);
        progreso = findViewById(R.id.pbWeb);

        String url = getIntent().getStringExtra(EXTRA_URL);
        if (url == null || url.trim().isEmpty()) {
            finish();
            return;
        }
        String h = Uri.parse(url).getHost();
        hostInicial = h == null ? "" : h.toLowerCase();

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);   // YouTube lo necesita para reproducir
        s.setDomStorageEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setBuiltInZoomControls(false);
        s.setAllowFileAccess(false);
        s.setAllowContentAccess(false);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest request) {
                if (permitido(request.getUrl())) {
                    return false;
                }
                Toast.makeText(VideoWebActivity.this,
                        "Ese enlace no se puede abrir aquí.", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView v, int avance) {
                progreso.setProgress(avance);
                progreso.setVisibility(avance >= 100 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onShowCustomView(View vista, CustomViewCallback callback) {
                if (vistaCompleta != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                vistaCompleta = vista;
                callbackCompleta = callback;
                raiz.addView(vista, new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }

            @Override
            public void onHideCustomView() {
                salirDePantallaCompleta();
            }
        });

        // El botón "atrás" del teléfono: sale de pantalla completa, retrocede en la página o cierra.
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (vistaCompleta != null) {
                    salirDePantallaCompleta();
                } else if (web.canGoBack()) {
                    web.goBack();
                } else {
                    finish();
                }
            }
        });

        if (savedInstanceState != null) {
            web.restoreState(savedInstanceState);
        } else {
            web.loadUrl(url);
        }
    }

    private boolean permitido(Uri uri) {
        String esquema = uri.getScheme();
        if (esquema == null || !(esquema.equals("https") || esquema.equals("http"))) {
            return false;
        }
        String host = uri.getHost();
        if (host == null) {
            return false;
        }
        host = host.toLowerCase();
        if (!hostInicial.isEmpty() && (host.equals(hostInicial) || host.endsWith("." + hostInicial))) {
            return true;
        }
        for (String ok : PERMITIDOS) {
            if (host.equals(ok) || host.endsWith("." + ok)) {
                return true;
            }
        }
        return false;
    }

    private void salirDePantallaCompleta() {
        if (vistaCompleta == null) {
            return;
        }
        raiz.removeView(vistaCompleta);
        vistaCompleta = null;
        if (callbackCompleta != null) {
            callbackCompleta.onCustomViewHidden();
            callbackCompleta = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (web != null) {
            web.saveState(outState);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (web != null) {
            web.onPause();   // detiene el audio si la persona sale de la pantalla
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (web != null) {
            web.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        if (web != null) {
            web.stopLoading();
            ((ViewGroup) web.getParent()).removeView(web);
            web.destroy();
            web = null;
        }
        super.onDestroy();
    }
}
