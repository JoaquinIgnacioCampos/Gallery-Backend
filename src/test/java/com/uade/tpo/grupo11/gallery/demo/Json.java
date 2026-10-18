package com.uade.tpo.grupo11.gallery.demo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Lector y escritor de JSON minimo (lo que habla la API de la demo). Asi el script no depende de que version de
// Jackson trae el back. Objetos = Map, arreglos = List, numeros = Long o Double, texto = String.
final class Json {

    private final String s;
    private int i;

    private Json(String s) {
        this.s = s;
    }

    // ------------------------------------------------------------------ leer
    static Object leer(String texto) {
        Json j = new Json(texto);
        j.blancos();
        Object valor = j.valor();
        j.blancos();
        if (j.i != j.s.length()) throw new IllegalArgumentException("sobra texto despues del JSON");
        return valor;
    }

    private void blancos() {
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;
    }

    private Object valor() {
        if (i >= s.length()) throw new IllegalArgumentException("JSON incompleto");
        char c = s.charAt(i);
        if (c == '{') return objeto();
        if (c == '[') return arreglo();
        if (c == '"') return texto();
        if (s.startsWith("true", i)) { i += 4; return Boolean.TRUE; }
        if (s.startsWith("false", i)) { i += 5; return Boolean.FALSE; }
        if (s.startsWith("null", i)) { i += 4; return null; }
        return numero();
    }

    private Map<String, Object> objeto() {
        Map<String, Object> m = new LinkedHashMap<>();
        i++;
        blancos();
        if (s.charAt(i) == '}') { i++; return m; }
        while (true) {
            blancos();
            String clave = texto();
            blancos();
            if (s.charAt(i++) != ':') throw new IllegalArgumentException("falta ':'");
            blancos();
            m.put(clave, valor());
            blancos();
            char c = s.charAt(i++);
            if (c == '}') return m;
            if (c != ',') throw new IllegalArgumentException("falta ',' o '}'");
        }
    }

    private List<Object> arreglo() {
        List<Object> l = new ArrayList<>();
        i++;
        blancos();
        if (s.charAt(i) == ']') { i++; return l; }
        while (true) {
            blancos();
            l.add(valor());
            blancos();
            char c = s.charAt(i++);
            if (c == ']') return l;
            if (c != ',') throw new IllegalArgumentException("falta ',' o ']'");
        }
    }

    private String texto() {
        if (s.charAt(i) != '"') throw new IllegalArgumentException("se esperaba un texto");
        StringBuilder sb = new StringBuilder();
        i++;
        while (true) {
            char c = s.charAt(i++);
            if (c == '"') return sb.toString();
            if (c != '\\') { sb.append(c); continue; }
            char e = s.charAt(i++);
            switch (e) {
                case 'n': sb.append('\n'); break;
                case 'r': sb.append('\r'); break;
                case 't': sb.append('\t'); break;
                case 'b': sb.append('\b'); break;
                case 'f': sb.append('\f'); break;
                case 'u': sb.append((char) Integer.parseInt(s.substring(i, i + 4), 16)); i += 4; break;
                default: sb.append(e);
            }
        }
    }

    private Object numero() {
        int desde = i;
        while (i < s.length() && "+-0123456789.eE".indexOf(s.charAt(i)) >= 0) i++;
        if (desde == i) throw new IllegalArgumentException("valor inesperado '" + s.charAt(i) + "'");
        BigDecimal n = new BigDecimal(s.substring(desde, i));
        return n.scale() <= 0 && n.precision() - n.scale() <= 18 ? (Object) n.longValue() : (Object) n.doubleValue();
    }

    // ------------------------------------------------------------------ escribir
    static String escribir(Object valor) {
        StringBuilder sb = new StringBuilder();
        escribir(valor, sb);
        return sb.toString();
    }

    private static void escribir(Object v, StringBuilder sb) {
        if (v == null) {
            sb.append("null");
        } else if (v instanceof Map) {
            sb.append('{');
            boolean primero = true;
            for (Map.Entry<?, ?> e : ((Map<?, ?>) v).entrySet()) {
                if (!primero) sb.append(',');
                primero = false;
                texto(String.valueOf(e.getKey()), sb);
                sb.append(':');
                escribir(e.getValue(), sb);
            }
            sb.append('}');
        } else if (v instanceof List) {
            sb.append('[');
            boolean primero = true;
            for (Object x : (List<?>) v) {
                if (!primero) sb.append(',');
                primero = false;
                escribir(x, sb);
            }
            sb.append(']');
        } else if (v instanceof BigDecimal) {
            sb.append(((BigDecimal) v).toPlainString());
        } else if (v instanceof Number || v instanceof Boolean) {
            sb.append(v);
        } else {
            texto(v.toString(), sb);
        }
    }

    static void texto(String t, StringBuilder sb) {
        sb.append('"');
        for (int k = 0; k < t.length(); k++) {
            char c = t.charAt(k);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
            }
        }
        sb.append('"');
    }
}
