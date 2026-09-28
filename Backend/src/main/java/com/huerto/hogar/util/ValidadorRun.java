package com.huerto.hogar.util;

public class ValidadorRun {
    private ValidadorRun() {}
    public static boolean esValido(String run) {
        if (run == null) return false;
        run = run.toUpperCase().trim();
        if (!run.matches("\\d{7,8}[0-9K]")) return false;
        String cuerpo = run.substring(0, run.length() - 1);
        char dvIngresado = run.charAt(run.length() - 1);
        return dvIngresado == calcularDv(cuerpo);
    }
    private static char calcularDv(String cuerpo) {
        int suma = 0; int[] serie = {2,3,4,5,6,7};
        for (int i = cuerpo.length()-1, j=0; i>=0; i--, j++)
            suma += (cuerpo.charAt(i)-'0') * serie[j%6];
        int r = 11-(suma%11);
        if (r==11) return '0'; if (r==10) return 'K';
        return (char)('0'+r);
    }
}
