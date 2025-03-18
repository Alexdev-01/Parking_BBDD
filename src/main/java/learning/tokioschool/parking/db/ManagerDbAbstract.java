package learning.tokioschool.parking.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Map;

import learning.tokioschool.parking.Coche;

public abstract class ManagerDbAbstract {

	static final String JDBC_DRIVER = "org.h2.Driver";
	//static final String DB_URL = "jdbc:h2:~/test";
	static final String DB_URL = "jdbc:h2:./src/main/resources/coches";
	
	
	// Database credentials
	static final String USER = "sa";
	static final String PASS = "";

	static final String CREATE_TABLE = "CREATE TABLE Coches (Matricula VARCHAR (10) NOT NULL PRIMARY KEY, Marca VARCHAR (10) NOT NULL, Modelo VARCHAR(20) NOT NULL, HoraEntrada DATETIME NOT NULL, HoraSalida DATETIME)";
	static final String SELECT_BY_MATRICULA = "SELECT Marca, Modelo, HoraEntrada, HoraSalida FROM Coches WHERE Matricula=?";
	static final String INSERT = "INSERT INTO Coches (Matricula, Marca, Modelo, HoraEntrada, HoraSalida) VALUES (?, ?, ?, ?, ?)";
	static final String UPDATE = "UPDATE Coches SET HoraSalida=? WHERE Matricula=?";
	static final String SELECT_ALL = "SELECT Matricula, Marca, Modelo, HoraEntrada, HoraSalida FROM Coches";
	static final String SELECT_ALL_WITHOUT_HORA_SALIDA = "SELECT Matricula, Marca, Modelo, HoraEntrada, HoraSalida FROM Coches WHERE HoraSalida IS NULL";

	public ManagerDbAbstract() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Inicializa conexion a base de datos
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	protected Connection iniConexion() throws ClassNotFoundException, SQLException {
		Class.forName(JDBC_DRIVER);
		System.out.println("Conectando a base de datos...");
		Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
		System.out.println("Conectado a base de datos...");
		return connection;
	}
			
	/**
	 * Crea tabla si no existe
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public void crearTabla() throws ClassNotFoundException, SQLException {
		try (Connection connection = iniConexion()) {
			try (Statement statement = connection.createStatement()) {
				// Execute a query
				System.out.println("Creando tabla en la base de datos especificada...");
				statement.executeUpdate(CREATE_TABLE);
				System.out.println("Creada tabla en la base de datos especificada...");
			} catch(SQLException sqlE) {
				System.out.println("Ya existe la tabla");
			}
		}
	}

	/**
	 * Actualiza un registro por matricula con la hora de salida
	 * @param matricula
	 * @param horaSalida
	 * @return
	 */
	public abstract int update(final String matricula, final LocalDateTime horaSalida);

	/**
	 * Inserta un registro en base de datos
	 * 
	 * @param matricula
	 * @param coche
	 * @return
	 */
	public abstract int insert(final String matricula, final Coche coche);

	/**
	 * Recupera un coche buscando por matricula
	 * @param matricula
	 * @return
	 */
	public abstract Coche search(final String matricula);

	/**
	 * Busca todos los coches del sistema
	 * @return
	 */
	public abstract Map<String, Coche> searchAll();

	/**
	 * Busca todos los coches del sistema filtrando por hora de salida igual a NULL
	 * @return
	 */
	public abstract Map<String, Coche> searchAllFilterHoraSalida();

}
