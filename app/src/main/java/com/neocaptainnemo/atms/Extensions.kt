package com.neocaptainnemo.atms

import android.app.Activity
import android.support.v4.app.Fragment
import dagger.android.AndroidInjection
import dagger.android.support.AndroidSupportInjection

fun Activity.daggerInject() = AndroidInjection.inject(this)

fun Fragment.daggerInject() = AndroidSupportInjection.inject(this)