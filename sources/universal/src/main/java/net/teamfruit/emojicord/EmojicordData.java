package net.teamfruit.emojicord;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class EmojicordData {
	public static final Gson gson = new Gson();

	private static void reportRead(final Exception e, final @Nullable String description) {
		if (description != null)
			Log.log.warn("Failed to load " + description + ": ", e);
	}

	private static void reportWrite(final Exception e, final @Nullable String description) {
		if (description != null)
			Log.log.warn("Failed to save " + description + ": ", e);
	}

	private static <T> T readStream(final InputStream stream, final Class<T> clazz) throws Exception {
		JsonReader reader = null;
		try {
			reader = new JsonReader(new InputStreamReader(stream, Charsets.UTF_8));
			return gson.fromJson(reader, clazz);
		} finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(stream);
		}
	}

	public static @Nullable <T> T loadStream(final InputStream stream, final Class<T> clazz,
			final @Nullable String description) {
		try {
			return readStream(stream, clazz);
		} catch (final Exception e) {
			reportRead(e, description);
		}
		return null;
	}

	private static <T> boolean writeStream(final OutputStream stream, final Class<T> clazz, final T object)
			throws Exception {
		JsonWriter writer = null;
		try {
			writer = new JsonWriter(new OutputStreamWriter(stream, Charsets.UTF_8));
			writer.setIndent("  ");
			gson.toJson(object, clazz, writer);
			return true;
		} finally {
			IOUtils.closeQuietly(writer);
			IOUtils.closeQuietly(stream);
		}
	}

	public static @Nullable <T> boolean saveStream(final OutputStream stream, final Class<T> clazz, final T object,
			final @Nullable String description) {
		try {
			return writeStream(stream, clazz, object);
		} catch (final Exception e) {
			reportWrite(e, description);
		}
		return false;
	}

	private static <T> T readFile(final File file, final Class<T> clazz) throws Exception {
		return readStream(FileUtils.openInputStream(file), clazz);
	}

	public static @Nullable <T> T loadFile(final File file, final Class<T> clazz, final @Nullable String description) {
		try {
			return readFile(file, clazz);
		} catch (final Exception e) {
			reportRead(e, description);
		}
		return null;
	}

	private static <T> boolean writeFile(final File file, final Class<T> clazz, final T object) throws Exception {
		return writeStream(FileUtils.openOutputStream(file), clazz, object);
	}

	public static @Nullable <T> boolean saveFile(final File file, final Class<T> clazz, final T object,
			final @Nullable String description) {
		try {
			return writeFile(file, clazz, object);
		} catch (final Exception e) {
			reportWrite(e, description);
		}
		return false;
	}

	private static InputStream getUrl(final String url) throws Exception {
		final HttpUriRequest req = new HttpGet(url);
		final HttpClientContext context = HttpClientContext.create();
		final HttpResponse response = Downloader.downloader.client.execute(req, context);
		final HttpEntity entity = response.getEntity();

		final int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK)
			throw new HttpResponseException(statusCode, "Invalid status code: " + url);

		return entity.getContent();
	}

	private static <T> T readUrl(final String url, final Class<T> clazz) throws Exception {
		return readStream(getUrl(url), clazz);
	}

	public static @Nullable <T> T loadUrl(final String url, final Class<T> clazz, final @Nullable String description) {
		try {
			return readUrl(url, clazz);
		} catch (final Exception e) {
			reportRead(e, description);
		}
		return null;
	}
}