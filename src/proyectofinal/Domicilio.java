/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectofinal;

/**
 *
 * @author Carlo Clases
 */
import java.util.UUID;

public class Domicilio {
    private final String id;
    private String direccion;
    private String ciudad;
    private String referencia;

    public Domicilio(String direccion, String ciudad, String referencia) {
        this.id = UUID.randomUUID().toString();
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.referencia = referencia;
    }

    public String getId() { return id; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }

    @Override
    public String toString() {
        return "Domicilio{id=" + id + ", " + direccion + ", " + ciudad + "}";
    }
}