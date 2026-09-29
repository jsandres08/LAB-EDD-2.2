package core;

import java.awt.Color;
import ui.Iconos;
import ui.Tema;

final class EstiloJuego {

    private EstiloJuego() {
    }

    static Color colorRol(RolJugador rol) {
        return switch (rol) {
            case CIUDADANO -> Tema.CIAN;
            case PERIODISTA -> Tema.AMBAR;
            case INFLUENCER -> Tema.MAGENTA;
            case ALCALDE -> Tema.VIOLETA;
        };
    }

    static Color colorRol(Object nombreRol) {
        for (RolJugador rol : RolJugador.values()) {
            if (nombreRol(rol).equals(String.valueOf(nombreRol)) || rol.name().equals(String.valueOf(nombreRol))) {
                return colorRol(rol);
            }
        }
        return Tema.GRIS;
    }

    static String nombreRol(RolJugador rol) {
        return switch (rol) {
            case CIUDADANO -> "Ciudadano";
            case PERIODISTA -> "Periodista";
            case INFLUENCER -> "Influencer";
            case ALCALDE -> "Candidato";
        };
    }

    static Color colorReputacion(int reputacion) {
        if (reputacion < 30) {
            return Tema.MAGENTA;
        }
        if (reputacion < 60) {
            return Tema.AMBAR;
        }
        return Tema.VERDE;
    }

    static Color colorOpcion(String opcion) {
        return switch (opcion) {
            case CatalogoPublicaciones.VERIFICAR, GameEngine.OPCION_CONTINUAR -> Tema.CIAN;
            case CatalogoPublicaciones.COMPARTIR -> Tema.VIOLETA;
            case CatalogoPublicaciones.REPORTAR -> Tema.AMBAR;
            default -> Tema.GRIS;
        };
    }

    static Iconos.Tipo iconoOpcion(String opcion) {
        return switch (opcion) {
            case CatalogoPublicaciones.VERIFICAR -> Iconos.Tipo.VERIFICAR;
            case CatalogoPublicaciones.COMPARTIR -> Iconos.Tipo.COMPARTIR;
            case CatalogoPublicaciones.REPORTAR -> Iconos.Tipo.BANDERA;
            case CatalogoPublicaciones.IGNORAR -> Iconos.Tipo.IGNORAR;
            case GameEngine.OPCION_CONTINUAR -> Iconos.Tipo.SIGUIENTE;
            default -> Iconos.Tipo.INFO;
        };
    }

    static Color colorCambio(EstadoCiudad.Indicador indicador, int delta) {
        boolean bueno = indicador.isPositivo() ? delta > 0 : delta < 0;
        return bueno ? Tema.VERDE : Tema.MAGENTA;
    }
}
