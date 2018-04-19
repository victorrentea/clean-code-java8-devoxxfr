package victor.clean.lambdas;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.jooq.lambda.Unchecked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;

// export all orders to a file

interface OrderRepo extends JpaRepository<Order, Long> { // J'aime Spring Data!
	Stream<Order> findByActiveTrue(); // 1 Mln orders ;)
}
class FileExporter {
	private final static Logger log = LoggerFactory.getLogger(FileExporter.class);
			
	public File exportFile(String fileName, Consumer<Writer> contentWriter) throws Exception {
		File file = new File("export/" + fileName);
		try (Writer writer = new FileWriter(file)) {
			contentWriter.accept(writer);
			return file;
		} catch (Exception e) {
			// TODO send email notification
			log.debug("Coucou!", e); // TERREUR !
			throw e;
		}
	}
	
	public static void main(String[] args) throws Exception {
		FileExporter fileExporter = new FileExporter();
		OrderExporterWriter orderExporterWriter = new OrderExporterWriter();
		UserExporterWriter userExporterWriter = new UserExporterWriter();
		
		fileExporter.exportFile("orders.txt", Unchecked.consumer(orderExporterWriter::writeContent));
		fileExporter.exportFile("users.txt", Unchecked.consumer(userExporterWriter::writeContent));
	}
}
class OrderExporterWriter {
	private OrderRepo repo;
	public void writeContent(Writer writer) throws IOException {
		writer.write("OrderID;Date\n");
		try (Stream<Order> stream = repo.findByActiveTrue()) {
			stream
				.map(o -> o.getId() + ";" + o.getCreationDate())
				.forEach(Unchecked.consumer(writer::write));
		}
	}
}

class UserExporterWriter extends FileExporter {
	public void writeContent(Writer writer) throws IOException {
		// Neahhh!
		// TODO write User export contnt instead
	}
}
// CR: implement the same export for Users
