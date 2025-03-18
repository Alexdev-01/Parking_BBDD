package learning.tokioschool.parking.db;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import learning.tokioschool.parking.Coche;

public class ManagerDbH2 extends ManagerDbAbstract {

	/**Actualiza un registro por matricula con la hora de salida*/
	@Override
	public int update(String matricula, LocalDateTime horaSalida) {
		//Creamos una conexion apartir del metodo iniConexion() de la class ManagerDbAbstract
		try (Connection connection = iniConexion();
				PreparedStatement pstmt = connection.prepareStatement(UPDATE)) {	//PreparedStatement, ejecuta la entencia guarada como "UPDATE"
			//Indicadores de la posicion de la sentencia: 1º horaSalida, 2º corresponde matricula
			pstmt.setTimestamp(1, Timestamp.valueOf(horaSalida));
			pstmt.setString(2, matricula);
			return pstmt.executeUpdate();	//envia el resultado de la sentencia
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return 0;	
	}
	
	
	/**Inserta un registro en base de datos*/
	@Override
	public int insert(String matricula, Coche coche) {
		try (Connection connection = iniConexion();
				PreparedStatement pstmt = connection.prepareStatement(INSERT)) {
			//Indicadores de la poscion de la sentencia: 1º matricula, 2º marca, 3º modelo, 4º horaEntrada, 5º horaSalida
			pstmt.setString(1, matricula);
			pstmt.setString(2, coche.getMarca());
			pstmt.setString(3, coche.getModelo());
			pstmt.setTimestamp(4, Timestamp.valueOf(coche.getHoraEntrada()));	//introduce la fecha y hora actual
			pstmt.setTimestamp(5, coche.getHoraSalida() != null ? Timestamp.valueOf(coche.getHoraSalida()) : null);	//asigna valor horaSalida o null si no hay una horaSalida registrada
			return pstmt.executeUpdate();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}

	
	/**Recupera un coche buscando por matricula*/
	@Override
	public Coche search(String matricula) {
		try (Connection connection = iniConexion();
				PreparedStatement pstmt = connection.prepareStatement(SELECT_BY_MATRICULA)) {
			pstmt.setString(1, matricula);
			ResultSet rs = pstmt.executeQuery();	//ejecutamos los resultado de la connsulta
			//imprime cada valor indicando el nombre de cada campo
			if (rs.next()) {
				return new Coche(
						rs.getString("Marca"),
						rs.getString("Modelo"),
						rs.getTimestamp("HoraEntrada").toLocalDateTime(),
						//SI HoraSalida es diferente null, asigna LocalDateTime. SI NO HoraSalida es igual null, asigna null
						rs.getTimestamp("HoraSalida") != null ? rs.getTimestamp("HoraSalida").toLocalDateTime() : null);	//si no tiene valor "HoraSalida" en la BBDD devolvera null	
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	
	/**Busca todos los coches del sistema*/
	@Override
	public Map<String, Coche> searchAll() {
		//creamos un Map con Matricula  y Coche
		Map<String, Coche> coches = new HashMap<>();
		try(Connection connection = iniConexion();
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(SELECT_ALL)) {	//utilizamos la consulta de ManagerDBAbstract
			//usamos un while para sacar todos los coches del sistema
			while (rs.next()) {
				Coche coche = new Coche(
						rs.getString("Marca"),
						rs.getString("Modelo"),
						rs.getTimestamp("HoraEntrada").toLocalDateTime(),
						//codigo de condiccion antes ? si es true : si es false 
						//SI tiene "HoraSalida" pasa "HoraSalida en LocalDateTime" : si NO tiene "horaSalida" es null
						rs.getTimestamp("HoraSalida") != null ? rs.getTimestamp("HoraSalida").toLocalDateTime() : null);
			coches.put(rs.getString("Matricula"), coche);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return coches;
	}

	
	/**Busca todos los coches del sistema filtrando por hora de salida igual a NULL*/
	@Override
	public Map<String, Coche> searchAllFilterHoraSalida() {
		//creamos un Map con Matricula  y Coche
		Map<String, Coche> coches = new HashMap<>();
		try(Connection connection = iniConexion();
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(SELECT_ALL_WITHOUT_HORA_SALIDA)) {
			//usamos un while para sacar todos los coches del sistema
			while (rs.next()) {
				Coche coche = new Coche(rs.getString("Marca"),
						rs.getString("Modelo"),
						rs.getTimestamp("HoraEntrada").toLocalDateTime(),
						null);
				coches.put(rs.getString("Matricula"), coche);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return coches;
	}

}
