package org.lunaris.api.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * It's very likely you don't have to use methods/constructors with this annotation yourself.
 * They are designed for internal usage only.
 */
@Retention(RetentionPolicy.SOURCE)
public @interface Internal {}
