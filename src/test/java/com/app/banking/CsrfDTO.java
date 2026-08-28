package com.app.banking;

public record CsrfDTO(
        String headerName,
        String parameterName,
        String token
) {
}
