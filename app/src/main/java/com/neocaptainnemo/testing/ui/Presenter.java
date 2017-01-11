package com.neocaptainnemo.testing.ui;

import android.content.Context;
import android.support.annotation.NonNull;

import com.neocaptainnemo.testing.model.AtmNode;
import com.neocaptainnemo.testing.model.ViewPort;
import com.neocaptainnemo.testing.service.Atms;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Scheduler;
import rx.Subscription;

public class Presenter {

    private Atms atms;
    private Context context;
    private IView view;
    private Scheduler subscribe;
    private Scheduler observe;
    private ViewPort viewPortBounds;
    private Observable<List<AtmNode>> observable;

    private Subscription atmsSubscription;
    private String filterStr;

    @Inject
    Presenter(Context context, Atms atms, @Named("subscribe") Scheduler subscribe,
              @Named("observe") Scheduler observe) {
        this.context = context;
        this.atms = atms;
        this.subscribe = subscribe;
        this.observe = observe;
        this.filterStr = "";
    }

    void onStart() {

    }

    void onStop() {
        if (atmsSubscription != null && !atmsSubscription.isUnsubscribed()) {
            atmsSubscription.unsubscribe();
        }
    }


    void fetchAtms(@NonNull ViewPort viewPort) {

        if (viewPortBounds == null || !viewPortBounds.isInside(viewPort)) {

            view.onStartGettingAtsm();
            observable = atms.request(viewPort).cache()
                    .doOnNext(atmNodes -> viewPortBounds = viewPort)
                    .doOnError((throwable) -> viewPortBounds = null);

            doFetch();
        }
    }

    private void doFetch() {
        if (atmsSubscription != null && !atmsSubscription.isUnsubscribed()) {
            atmsSubscription.unsubscribe();
        }
        atmsSubscription = observable
                .map(atmNodes -> {
                    if (filterStr.isEmpty()) {
                        return atmNodes;
                    }

                    List<AtmNode> res = new ArrayList<>();

                    for (AtmNode atmNode : atmNodes) {
                        if (atmNode.getTags() != null && atmNode.getTags().getName() != null &&
                                atmNode.getTags().getName().toLowerCase().contains(filterStr.toLowerCase())) {
                            res.add(atmNode);
                        }
                    }

                    return res;
                })
                .subscribeOn(subscribe)
                .observeOn(observe)
                .subscribe(atmNodes -> {
                    view.onGotAtms(atmNodes);
                }, throwable -> {
                    view.onAtmsFailed();
                });
    }

    void setFilter(String filter) {
        this.filterStr = filter.trim();
        doFetch();
    }

    void setView(IView view) {
        this.view = view;
    }
}
