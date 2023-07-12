package springbatch.utilize.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NoHttpResponseException;
import org.apache.http.conn.ConnectTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import springbatch.utilize.exception.RestException;
import springbatch.utilize.exception.RestTimeoutException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;


@Slf4j
@Configuration
public class RestTemplateUtil {

    private RestOperations restOperations;

    public RestTemplateUtil(RestOperations restOperations) {
        this.restOperations = restOperations;
    }

    public static final int ERR_INTERNAL_ERROR = 4006;
    public static final int ERR_REST_ERROR = 4007;
    public static final int ERR_REST_CONNECTION_ERROR = 4008;
    public static final int ERR_REST_TIMEOUT_ERROR = 4009;

    public RestTemplateUtil() {
        log.info("Init...");
    }

    public HttpEntity<String> getDefaultHttpEntity() {
        return new HttpEntity<String>(getDefaultHttpHeader());
    }

    public HttpEntity<String> getDefaultHttpEntity(String body) {
        return new HttpEntity<String>(body, getDefaultHttpHeader());
    }

    public HttpHeaders getDefaultHttpHeader() {
        HttpHeaders headers = new HttpHeaders();

        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }

    public HttpHeaders getBearerAuthHttpHeader(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }

    public HttpEntity<String> getBearerAuthHttpEntity(String body, String accessToken) {
        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(accessToken);

        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<String>(body, headers);
    }

    // URI Base Request : GET, DELETE : HttpEntity Use Default
    public ResponseEntity<String> sendByUri(UriComponentsBuilder builder, HttpMethod httpMethod) {
        return sendByUri(builder, httpMethod, null);
    }

    // URI Base Request : GET, DELETE : HttpEntity Free
    public ResponseEntity<String> sendByUri(UriComponentsBuilder builder, HttpMethod httpMethod, HttpEntity<?> requestEntity) {
        //
        ResponseEntity<String> responseEntity = null;

        for (int i = 0; i < 2; i++) {
            try {
                responseEntity = restOperations.exchange(builder.build().encode().toUri(), httpMethod,
                        (null == requestEntity ? getDefaultHttpEntity() : requestEntity), String.class);
            } catch (RestClientException rce) {
                Throwable nestedException = rce.getCause();
                if(nestedException instanceof ConnectTimeoutException) {
                    log.info("==== nestedException:: ConnectTimeoutException message: " + nestedException.getMessage());
                }
                else if (nestedException instanceof SocketTimeoutException) {
                    log.info("==== nestedException:: SocketTimeoutException message: " + nestedException.getMessage());
                }

                log.error("RestClientException error: ", rce);
                if ((rce.getRootCause() instanceof ConnectException) || (rce.getRootCause() instanceof ConnectTimeoutException)
                        || (rce.getRootCause() instanceof UnknownHostException)) {
                    // internal server connection error
                    // network down, relay server down
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        log.error("error: ", e.toString());
                    }
                    log.info("RestClientException message: " + rce.getMessage());
                    log.info("RestClientException try: " + String.valueOf(i+1));
                    continue;

                } else if (rce.getRootCause() instanceof NoHttpResponseException) {
                    try {
                        Thread.sleep(100);
                    } catch (Exception er) {
                        log.error("error: ", er.toString());
                    }
                    log.info("NoHttpResponseException message: " + rce.getMessage());
                    log.info("NoHttpResponseException try: " + String.valueOf(i+1));
                    continue;

                } else if (rce.getRootCause() instanceof SocketTimeoutException) {
                    // network down, relay server down
                    // it processed normally, but not processed within 10 seconds
                    log.info("RestTimeoutException message: " + rce.getMessage());
                    throw new RestTimeoutException(ERR_REST_TIMEOUT_ERROR, "서버 시간초과 오류");
                } else {
                    log.info("RestException message: " + rce.getMessage());
                    throw new RestException(ERR_REST_ERROR, "서버 알수없는 오류");
                }
            } catch (Exception e) {
                log.info("Exception message: " + e.getMessage());
                log.error("Exception error: ", e);
                throw new RestException(ERR_REST_ERROR, "서버 알수없는 오류");
            }

            // operation is ok
            break;
        }

        if (responseEntity == null) {
            // connection fail or unknown error
            throw new RestException(ERR_REST_CONNECTION_ERROR, "서버 연결 오류");
        }

        return responseEntity;
    }

    // Body Base Request : POST, PUT, PATCH : HttpEntity Use Default
    public ResponseEntity<String> sendByBody(UriComponentsBuilder builder, HttpMethod httpMethod, Object obj) {
        return sendByBody(builder, httpMethod, obj, null);
    }

    public ResponseEntity<String> sendByBody(UriComponentsBuilder builder, HttpMethod httpMethod, Object obj, String accessToken) {
        //
        String body = null;
        ResponseEntity<String> responseEntity = null;

        if (obj instanceof String) {
            body = (String) obj;
        } else {
            try {
                body = objectToJsonString(obj);
            } catch (Exception e) {
                throw new RestException(ERR_INTERNAL_ERROR, "JSON 파싱 에러");
            }
        }

        for (int i = 0; i < 2; i++) {
            try {
                responseEntity = restOperations.exchange(builder.toUriString(), httpMethod, null == accessToken ? getDefaultHttpEntity(body) : getBearerAuthHttpEntity(body, accessToken), String.class);
            } catch (RestClientException rce) {
                Throwable nestedException = rce.getCause();
                if(nestedException instanceof ConnectTimeoutException) {
                    log.info("==== nestedException:: ConnectTimeoutException message: " + nestedException.getMessage());
                }
                else if (nestedException instanceof SocketTimeoutException) {
                    log.info("==== nestedException:: SocketTimeoutException message: " + nestedException.getMessage());
                }

                log.error("RestClientException error: ", rce);
                if ((rce.getRootCause() instanceof ConnectException) || (rce.getRootCause() instanceof ConnectTimeoutException)
                        || (rce.getRootCause() instanceof UnknownHostException)) {
                    // internal server connection error
                    // network down, relay server down
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        log.error("error: ", e.toString());
                    }
                    log.info("RestClientException message: " + rce.getMessage());
                    log.info("RestClientException try: " + String.valueOf(i+1));
                    continue;

                } else if (rce.getRootCause() instanceof NoHttpResponseException) {
                    try {
                        Thread.sleep(100);
                    } catch (Exception er) {
                        log.error("error: ", er.toString());
                    }
                    log.info("NoHttpResponseException message: " + rce.getMessage());
                    log.info("NoHttpResponseException try: " + String.valueOf(i+1));
                    continue;

                } else if (rce.getRootCause() instanceof SocketTimeoutException) {
                    // network down, relay server down
                    // it processed normally, but not processed within 10 seconds
                    log.info("RestTimeoutException message: " + rce.getMessage());
                    throw new RestTimeoutException(ERR_REST_TIMEOUT_ERROR, "서버 시간초과 오류");
                } else {
                    log.info("RestException message: " + rce.getMessage());
                    throw new RestException(ERR_REST_ERROR, "서버 알수없는 오류");
                }
            }
            catch (Exception e) {
                log.info("Exception message: " + e.getMessage());
                log.error("Exception error: ", e);
                throw new RestException(ERR_REST_ERROR, "서버 알수없는 오류");
            }

            // operation is ok
            break;
        }

        if (responseEntity == null) {
            // connection fail or unknown error
            throw new RestException(ERR_REST_CONNECTION_ERROR, "서버 연결 오류");
        }

        return responseEntity;
    }

    public ResponseEntity<String> sendHeadersByBody(UriComponentsBuilder builder, HttpMethod httpMethod, Object obj, HttpHeaders headers) {
        //
        String body = null;

        if (obj instanceof String) {
            body = (String) obj;
        } else {
            try {
                body = objectToJsonString(obj);
            } catch (Exception e) {
                throw new RestException(ERR_INTERNAL_ERROR, "JSON 파싱 에러");
            }
        }

        HttpEntity<String> requestEntity = new HttpEntity<String>(body, headers);
        ResponseEntity<String> responseEntity = null;

        for (int i = 0; i < 2; i++) {
            try {
                responseEntity = restOperations.exchange(builder.toUriString(), httpMethod, requestEntity, String.class);
            } catch (RestClientException rce) {
                Throwable nestedException = rce.getCause();
                if(nestedException instanceof ConnectTimeoutException) {
                    log.info("==== nestedException:: ConnectTimeoutException message: " + nestedException.getMessage());
                }
                else if (nestedException instanceof SocketTimeoutException) {
                    log.info("==== nestedException:: SocketTimeoutException message: " + nestedException.getMessage());
                }

                log.error("RestClientException error: ", rce);
                if ((rce.getRootCause() instanceof ConnectException) || (rce.getRootCause() instanceof ConnectTimeoutException)
                        || (rce.getRootCause() instanceof UnknownHostException)) {
                    // internal server connection error
                    // network down, relay server down
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        log.error("error: ", e.toString());
                    }
                    log.info("RestClientException message: " + rce.getMessage());
                    log.info("RestClientException try: " + String.valueOf(i+1));
                    continue;

                } else if (rce.getRootCause() instanceof NoHttpResponseException) {
                    try {
                        Thread.sleep(100);
                    } catch (Exception er) {
                        log.error("error: ", er.toString());
                    }
                    log.info("NoHttpResponseException message: " + rce.getMessage());
                    log.info("NoHttpResponseException try: " + String.valueOf(i+1));
                    continue;

                } else if (rce.getRootCause() instanceof SocketTimeoutException) {
                    // network down, relay server down
                    // it processed normally, but not processed within 10 seconds
                    log.info("RestTimeoutException message: " + rce.getMessage());
                    throw new RestTimeoutException(ERR_REST_TIMEOUT_ERROR, "서버 시간초과 오류");
                } else {
                    log.info("RestException message: " + rce.getMessage());
                    throw new RestException(ERR_REST_ERROR, "서버 알수없는 오류");
                }
            } catch (Exception e) {
                log.info("Exception message: " + e.getMessage());
                log.error("Exception error: ", e);
                throw new RestException(ERR_REST_ERROR, "서버 알수없는 오류");
            }

            // operation is ok
            break;
        }

        if (responseEntity == null) {
            // connection fail or unknown error
            throw new RestException(ERR_REST_CONNECTION_ERROR, "서버 연결 오류");
        }

        return responseEntity;
    }

    public static String objectToJsonString(Object src) {
        try {
            return new ObjectMapper().writeValueAsString(src);
        } catch (Exception e) {
            return null;
        }
    }
}
