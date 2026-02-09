/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectofinal;

/**
 *
 * @author Carlo Clases
 */
public class Tecnico extends Persona {
    private String especialidad;

    public Tecnico(String nombre, String telefono, String email, String especialidad) {
        super(nombre, telefono, email);
        this.especialidad = especialidad;
    }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    @Override
    public String toString() {
        return "Tecnico{id=" + getId() + ", nombre=" + getNombre() + ", esp=" + especialidad + "}";
    }
}