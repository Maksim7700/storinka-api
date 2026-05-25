package ua.storinka.backend.enums;

/**
 * Which page initiated a Google sign-in request. Lets the backend reject
 * "login" attempts for emails that have never registered — instead of
 * silently creating a new account on the login screen.
 */
public enum AuthIntent {
    LOGIN,
    REGISTER
}
