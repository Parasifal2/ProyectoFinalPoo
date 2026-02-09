/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectofinal;

/**
 *
 * @author Carlo Clases
 */
public class Cliente extends Persona {
    public Cliente(String nombre, String telefono, String email) {
        super(nombre, telefono, email);
    }

    @Override
    public String toString() {
        return "Cliente{id=" + getId() + ", nombre=" + getNombre() + "}";
    }
}