package com.kinastudio.webapp.dominio;

//clase para validar el formato de un email y no repetir codigo
public final class ValidadorEmail {

    private ValidadorEmail() {
    }

    public static boolean esValido(String email) {
        if (email == null || email.isBlank() || email.chars().anyMatch(Character::isWhitespace)) {
            return false;
        }
        int arroba = email.indexOf('@');
        if (arroba <= 0 || arroba != email.lastIndexOf('@')) {
            return false;
        }
        String dominio = email.substring(arroba + 1);
        int punto = dominio.indexOf('.');
        return punto > 0 && !dominio.endsWith(".") && !dominio.contains("..");
    }
}
