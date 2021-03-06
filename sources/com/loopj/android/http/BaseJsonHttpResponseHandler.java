package com.loopj.android.http;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpStatus;

public abstract class BaseJsonHttpResponseHandler<JSON_TYPE> extends TextHttpResponseHandler {
    private static final String LOG_TAG = "BaseJsonHttpRH";

    public abstract void onFailure(int i, Header[] headerArr, Throwable th, String str, JSON_TYPE json_type);

    public abstract void onSuccess(int i, Header[] headerArr, String str, JSON_TYPE json_type);

    protected abstract JSON_TYPE parseResponse(String str, boolean z) throws Throwable;

    public BaseJsonHttpResponseHandler() {
        this("UTF-8");
    }

    public BaseJsonHttpResponseHandler(String encoding) {
        super(encoding);
    }

    public final void onSuccess(final int statusCode, final Header[] headers, final String responseString) {
        if (statusCode != HttpStatus.SC_NO_CONTENT) {
            Runnable parser = new Runnable() {
                public void run() {
                    try {
                        final JSON_TYPE jsonResponse = BaseJsonHttpResponseHandler.this.parseResponse(responseString, false);
                        BaseJsonHttpResponseHandler.this.postRunnable(new Runnable() {
                            public void run() {
                                BaseJsonHttpResponseHandler.this.onSuccess(statusCode, headers, responseString, jsonResponse);
                            }
                        });
                    } catch (final Throwable t) {
                        AsyncHttpClient.log.mo1729d(BaseJsonHttpResponseHandler.LOG_TAG, "parseResponse thrown an problem", t);
                        BaseJsonHttpResponseHandler.this.postRunnable(new Runnable() {
                            public void run() {
                                BaseJsonHttpResponseHandler.this.onFailure(statusCode, headers, t, responseString, null);
                            }
                        });
                    }
                }
            };
            if (getUseSynchronousMode() || getUsePoolThread()) {
                parser.run();
                return;
            } else {
                new Thread(parser).start();
                return;
            }
        }
        onSuccess(statusCode, headers, null, null);
    }

    public final void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        if (responseString != null) {
            final String str = responseString;
            final int i = statusCode;
            final Header[] headerArr = headers;
            final Throwable th = throwable;
            Runnable parser = new Runnable() {

                /* renamed from: com.loopj.android.http.BaseJsonHttpResponseHandler$2$2 */
                class C07182 implements Runnable {
                    C07182() {
                    }

                    public void run() {
                        BaseJsonHttpResponseHandler.this.onFailure(i, headerArr, th, str, null);
                    }
                }

                public void run() {
                    try {
                        final JSON_TYPE jsonResponse = BaseJsonHttpResponseHandler.this.parseResponse(str, true);
                        BaseJsonHttpResponseHandler.this.postRunnable(new Runnable() {
                            public void run() {
                                BaseJsonHttpResponseHandler.this.onFailure(i, headerArr, th, str, jsonResponse);
                            }
                        });
                    } catch (Throwable t) {
                        AsyncHttpClient.log.mo1729d(BaseJsonHttpResponseHandler.LOG_TAG, "parseResponse thrown an problem", t);
                        BaseJsonHttpResponseHandler.this.postRunnable(new C07182());
                    }
                }
            };
            if (getUseSynchronousMode() || getUsePoolThread()) {
                parser.run();
                return;
            } else {
                new Thread(parser).start();
                return;
            }
        }
        onFailure(statusCode, headers, throwable, null, null);
    }
}
