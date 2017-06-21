package co.louiscap.libs.zipper;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ZipperTest {
	@Test
	public void compress() throws Exception {
		Zipper zipper = new Zipper();
		zipper.compress("test_files/compress.zip", new String[] {
			"test_files/one.png",
			"test_files/two.png",
			"test_files/three.png",
		});
	}
	@Test
	public void strips_prefix() throws Exception {
		Zipper zipper = new Zipper();

		zipper.setPrefix("test_files/");
		zipper.compress("test_files/strips_prefix.zip", new String[] {
			"test_files/one.png",
			"test_files/two.png",
			"test_files/three.png",
		});
	}

	@Test
	public void compress_bytearray() throws Exception {
		Zipper zipper = new Zipper();
		String json = "{\n\t\"my_num\": 123,\n\t\"my_string\": \"This is a string whatsit\"\n}";
		Map<String, byte[]> data = new HashMap<>();

		data.put("state.json", json.getBytes());

		zipper.compress("test_files/compress_bytearray.zip", data);
	}
}