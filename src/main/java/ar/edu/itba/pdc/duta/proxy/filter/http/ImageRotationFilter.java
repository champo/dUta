package ar.edu.itba.pdc.duta.proxy.filter.http;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.admin.Stats;
import ar.edu.itba.pdc.duta.http.MessageFactory;
import ar.edu.itba.pdc.duta.http.model.MediaType;
import ar.edu.itba.pdc.duta.http.model.Message;
import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.net.buffer.DataBuffer;
import ar.edu.itba.pdc.duta.proxy.filter.Filter;
import ar.edu.itba.pdc.duta.proxy.filter.FilterPart;
import ar.edu.itba.pdc.duta.proxy.filter.Interest;
import ar.edu.itba.pdc.duta.proxy.operation.Operation;

public class ImageRotationFilter implements Filter {

	{
		Stats.registerFilterType(ImageRotationFilter.class);
	}

	@Override
	public FilterPart getRequestPart() {

		return null;
	}

	@Override
	public FilterPart getResponsePart() {

		return new ResponsePart();
	}

	private static class ResponsePart extends FilterPart {

		private Logger logger = Logger.getLogger(ResponsePart.class);

		private MediaType contentType;

		@Override
		public Interest checkInterest(MessageHeader header) {

			boolean isImage = false;

			contentType = MediaType.valueOf(header.getField("Content-Type")); 

			if (MediaType.valueOf("image/*").isCompatible(contentType)) {

				isImage = true;
			}

			return new Interest(false, false, isImage);
		}

		@Override
		public Message filter(Operation op, Message msg) {

			try {

				byte[] bytes = msg.getBody().read();

		        Iterator<ImageReader> readers = ImageIO.getImageReadersByMIMEType(contentType.toString());
		        ImageReader reader = readers.next();
		        reader.setInput(ImageIO.createImageInputStream(new ByteArrayInputStream(bytes)));
		        BufferedImage image = reader.read(0);

		        AffineTransform tx = AffineTransform.getScaleInstance(-1, -1);
		        tx.translate(-image.getWidth(null), -image.getHeight(null));
		        AffineTransformOp txop = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
				image = txop.filter(image, null);

				Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType(contentType.toString());
				ImageWriter writer = writers.next();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				writer.setOutput(ImageIO.createImageOutputStream(baos));
				writer.write(image);

				baos.flush();
				bytes = baos.toByteArray();
				baos.close();

				msg.setBody(new DataBuffer(bytes));

			} catch (IOException e) {

				logger.error("Failed to read/write image", e);
				return MessageFactory.build500();

			}  catch (NoSuchElementException e) {

				logger.error("Invalid image type", e);
				return MessageFactory.build500();
			}

			return null;
		}
	}

	@Override
	public int getPriority() {

		return 0;
	}
}
