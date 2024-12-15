package com.oyetech.cripto.apiParams;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.lsposed.lsparanoid.Obfuscate;

/**
 * Created by Erdi Özbek
 * -5.03.2022-
 * -23:39-
 **/

@Obfuscate
public class ApiPostParams {
    @NotNull
    public static final String CLIENT_SECRET_KEY = "Client-Secret";
    @NotNull
    public static final String CLIENT_UNIQ_ID = "Client-Unique-Id";
    @NotNull
    public static final String AUTH_KEY = "Authorization";
    @NonNull
    public static String UserAgent = "UserAgent";
    @NonNull
    public static String authenticate = "authenticate";
}
