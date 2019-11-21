package se.kth.common;

import java.io.Serializable;

/**
 *  Class for making messages between client and server
 */
public class Message implements Serializable {
    private final MessageType messageType;
    private final Object body;

    /**
     *  Creates a message
     * @param messageType The message type to be made
     * @param body The message
     */
    public Message(MessageType messageType, Object body){
        this.messageType = messageType;
        this.body = body;
    }

    public Object getBody(){
        return this.body;
    }

    public MessageType getMessageType(){
        return this.messageType;
    }

    @Override
    public String toString() {
        return "Message{" + "type=" + messageType + ", body=" + body + '}';
    }

    /**
     *  Serializes the message for sending.
     * @param message (Message) The message to be serialized
     * @return (String) The serialized message
     */
    public static String serialize(Message message) {
        return message.messageType.toString() + "##" + message.body;
    }

    /**
     *  Deserialize a message for receiving.
     * @param message (String) The serialized message
     * @return (Message) The deserialized message
     */
    public static Message deserialize(String message) {
        String[] parts = message.split("##");
        MessageType type = MessageType.valueOf(parts[0].toUpperCase());
        String body = parts.length > 1 ? parts[1] : "";
        return new Message(type, body);
    }
}
