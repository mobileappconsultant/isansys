// Generated by Dagger (https://dagger.dev).
package com.permissions;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class PermissionsImpl_Factory implements Factory<PermissionsImpl> {
  private final Provider<Context> contextProvider;

  public PermissionsImpl_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public PermissionsImpl get() {
    return newInstance(contextProvider.get());
  }

  public static PermissionsImpl_Factory create(Provider<Context> contextProvider) {
    return new PermissionsImpl_Factory(contextProvider);
  }

  public static PermissionsImpl newInstance(Context context) {
    return new PermissionsImpl(context);
  }
}
