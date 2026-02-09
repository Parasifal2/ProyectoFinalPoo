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

public abstract class Persona {
    private final String id;
    private String nombre;
    private String telefono;
    private String email;

    protected Persona(String nombre, String telefono, String email) {
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
