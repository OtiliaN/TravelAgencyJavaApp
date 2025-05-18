package org.example.network.rpcprotocol;

import java.io.Serializable;

public class Response implements Serializable {
    private ResponseType type;
    private Object data;

    private Response(){};

    public ResponseType type(){
        return type;
    }

    public Object data(){
        return data;
    }

    public static class Builder{
        private Response response = new Response();

        public Builder type(ResponseType type){
            response.type = type;
            return this;
        }

        public Builder data(Object data){
            response.data = data;
            return this;
        }

        public Response build(){
            return response;
        }
    }

    public void data(Object data){
        this.data = data;
    }

    public void type(ResponseType type){
        this.type = type;
    }
}
