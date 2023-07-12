package springbatch.utilize.exception;

public class RestException extends RuntimeException {
	private static final long serialVersionUID = -100291454471714395L;

	private int code;
	private String message;

	public RestException(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
