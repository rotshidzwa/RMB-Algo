package za.co.rmb.algo.app.model;
import za.co.rmb.algo.app.exception.OrderReaderException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Iterator;

import javax.xml.stream.events.Attribute;

public class OrderReader implements Closeable, AutoCloseable {
        private XMLEventReader eventReader;

        class ElementNames {
            static final String ADD_ORDER = "AddOrder";

            static final String DELETE_ORDER = "DeleteOrder";
        }

        class AttributesLocalParts {
            static final String BOOK = "book";

            static final String ORDER_ID = "orderId";

            static final String OPERATION = "operation";

            static final String PRICE = "price";

            static final String VOLUME = "volume";
        }

         public static OrderReader from(File file) {
            if(file != null){
                if(file.isFile()){
                    try {
                        return new OrderReader(file);
                    } catch (FileNotFoundException | XMLStreamException e) {
                        throw new OrderReaderException(e);
                    }
                }
            }
            return null;

        }

        /** Creates a reader to get data from given stream. */
        public static OrderReader from(InputStream inputStream) {
            if(inputStream !=null) {
                try {
                    return new OrderReader(inputStream);
                } catch (XMLStreamException e) {
                    throw new OrderReaderException(e);
                }
            }
            return null;
        }

	private OrderReader(File file) throws FileNotFoundException, XMLStreamException {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            eventReader = factory.createXMLEventReader(new FileReader(file));
        }

	private OrderReader(InputStream inputStream) throws XMLStreamException {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            eventReader = factory.createXMLEventReader(new InputStreamReader(inputStream));
        }

        public boolean hasNext() {
            return eventReader.hasNext();
        }


        @SuppressWarnings("unchecked")
        public OrderMessage next() {
            OrderMessage message = OrderMessage.EMPTY;

            try {
                XMLEvent event = null;

                // Read first XML start element
                do {
                    event = eventReader.nextEvent();
                } while (eventReader.hasNext() && !event.isStartElement());

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    if (ElementNames.ADD_ORDER.equals(startElement.getName().getLocalPart())) {
                        message = new AddOrderMessage();

                        populateOrderMessage(message.asAddMessage(), startElement.getAttributes());
                    } else if (ElementNames.DELETE_ORDER.equals(startElement.getName().getLocalPart())) {
                        message = new DeleteOrderMessage();

                        populateOrderMessage(message, startElement.getAttributes());
                    }
                }
            } catch (XMLStreamException e) {
                throw new OrderReaderException(e);
            }

            return message;
        }

        public void close() {
            try {
                eventReader.close();
            } catch (XMLStreamException e) {
                throw new OrderReaderException(e);
            }
        }

        private void populateOrderMessage(OrderMessage message, Iterator<Attribute> attributes) {
            while (attributes.hasNext()) {
                Attribute attribute = attributes.next();
                switch (attribute.getName().getLocalPart()) {
                    case AttributesLocalParts.BOOK :
                        message.setBookId(attribute.getValue());
                        break;
                    case AttributesLocalParts.ORDER_ID:
                        if (attribute.getValue() !=null || attribute.getValue() != "") {
                            message.setOrderId(Long.valueOf(attribute.getValue()));
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        private void populateOrderMessage(AddOrderMessage message, Iterator<Attribute> attributes) {
            while (attributes.hasNext()) {
                Attribute attribute = attributes.next();
                if (attribute.getValue() == null || attribute.getValue() == "") {
                    continue;
                }

                switch (attribute.getName().getLocalPart()) {
                    case AttributesLocalParts.BOOK :
                        message.setBookId(attribute.getValue());
                        break;
                    case AttributesLocalParts.ORDER_ID:
                        message.setOrderId(Long.valueOf(attribute.getValue()));
                        break;
                    case AttributesLocalParts.OPERATION:
                        message.setOperation(attribute.getValue());
                        break;
                    case AttributesLocalParts.PRICE:
                        BigDecimal price = BigDecimal.valueOf(Double.valueOf(attribute.getValue()));
                        message.setPrice(price);
                        break;
                    case AttributesLocalParts.VOLUME:
                        Long volume = Long.valueOf(attribute.getValue());
                        message.setVolume(volume);
                        break;
                    default:
                        throw new OrderReaderException();
                }
            }
        }
}
