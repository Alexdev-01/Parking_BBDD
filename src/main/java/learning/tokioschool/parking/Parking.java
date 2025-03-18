package learning.tokioschool.parking;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import learning.tokioschool.parking.db.ManagerDbH2;

public class Parking {

	//final Map<String, Coche> parking;
	//se crea un instancia de nuestra BBDD para poder trabajar con ella
	private ManagerDbH2 managerDbH2;

	public Parking() {
		this.managerDbH2 = new ManagerDbH2();
		try {
			managerDbH2.crearTabla();	//metodo para crear una tabla
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Metodo que comprueba si existe un coche en el sistema
	 * 
	 * @param matricula
	 * @return
	 */
	public boolean existeCoche(final String matricula) {
		return managerDbH2.search(matricula) != null;
	}

	/**
	 * Metodo que obtiene un coche del sistema
	 * 
	 * @param matricula
	 * @return
	 */
	public Coche getCoche(final String matricula) {
		return managerDbH2.search(matricula);
	}

	/**
	 * Metodo que añade un coche al sistema
	 * 
	 * @param matricula
	 * @param coche
	 */
	public void putCoche(final String matricula, final Coche coche) {
		if (managerDbH2.search(matricula) == null) {
			managerDbH2.insert(matricula, coche);
		}else {
			System.out.println("Coche ya esta existe");
		}
	}

	/**
	 * Metodo que imprime todos los coches del sistema, tanto los que estan dentro del parking como los que no.
	 */
	public void imprimirCochesSistema() {        
		try {
			managerDbH2.searchAll().forEach((k, v) -> {
				System.out.println("Matricula: " + k + " Datos del " + v);
			});
		} catch (Exception ex) {
			System.out.println("Error al imprimir coches en el sistema");
		}
		
	}

	/**
	 * Metodo que imprime los coches que estan dentro del parking (horaSalida = null)
	 */
	public void imprimirCochesParking() {
		try {
			managerDbH2.searchAllFilterHoraSalida().forEach((k, v) -> {
				if (v.getHoraSalida() == null) {
					System.out.println("Matricula: " + k + " Datos del " + v);
				}
			});
		} catch (Exception ex) {
			System.out.println("Error al imprimir coches en el parking");
		}
		
	}

	/**
	 * Método que calcula la cantidad a pagar por un coche según el tiempo que ha estado dentro del parking
	 * @param matricula
	 */
	public void cantidadAPagar(final String matricula) {
		//creamos un objeto de la matricula introducida por parametro, pero primero comprobamos en la BBDD que existe
		Coche coche = managerDbH2.search(matricula);
		//comprueba q coche exite y que la HoraSalida es null
		if (coche != null && coche.getHoraSalida() == null) {
			coche.setHoraSalida(LocalDateTime.now());	//se añade horaSalida actual
			managerDbH2.update(matricula, coche.getHoraSalida());
			System.out.println("Cantidad a pagar " + coche.cantidadAPagar());
		} else {
			System.out.println("Fallo al calcular cantidadAPagar");
		}
	}

}
