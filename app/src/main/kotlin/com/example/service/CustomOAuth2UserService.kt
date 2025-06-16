package com.example.service

import org.slf4j.LoggerFactory
import org.springframework.core.ParameterizedTypeReference
import org.springframework.core.convert.converter.Converter
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequestEntityConverter
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2AuthorizationException
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority
import org.springframework.util.Assert
import org.springframework.util.StringUtils
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestOperations
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.UnknownContentTypeException

class CustomOAuth2UserService(restTemplate: RestTemplate) : DefaultOAuth2UserService() {
    private val log = LoggerFactory.getLogger(CustomOAuth2UserService::class.java)
    private val restOperations: RestOperations

    init {
        this.restOperations = restTemplate
        setRestOperations(restTemplate)
    }

    private val MISSING_USER_INFO_URI_ERROR_CODE = "missing_user_info_uri"
    private val MISSING_USER_NAME_ATTRIBUTE_ERROR_CODE = "missing_user_name_attribute"
    private val INVALID_USER_INFO_RESPONSE_ERROR_CODE = "invalid_user_info_response"
    private val PARAMETERIZED_RESPONSE_TYPE: ParameterizedTypeReference<Map<String, Any>> = object : ParameterizedTypeReference<Map<String, Any>>() {}
    private val requestEntityConverter: Converter<OAuth2UserRequest, RequestEntity<*>> = OAuth2UserRequestEntityConverter()

    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        Assert.notNull(userRequest, "userRequest cannot be null")
        if (!StringUtils
                        .hasText(userRequest.clientRegistration.providerDetails.userInfoEndpoint.uri)) {
            val oauth2Error = OAuth2Error(MISSING_USER_INFO_URI_ERROR_CODE, "Missing required UserInfo Uri in UserInfoEndpoint for Client Registration: "
                    + userRequest.clientRegistration.registrationId,
                    null)
            throw OAuth2AuthenticationException(oauth2Error, oauth2Error.toString())
        }
        val userNameAttributeName = userRequest.clientRegistration
                .providerDetails
                .userInfoEndpoint
                .userNameAttributeName
        if (!StringUtils.hasText(userNameAttributeName)) {
            val oauth2Error = OAuth2Error(MISSING_USER_NAME_ATTRIBUTE_ERROR_CODE, (
                    "Missing required \"user name\" attribute name in UserInfoEndpoint for Client Registration: "
                            + userRequest.clientRegistration.registrationId),
                    null)
            throw OAuth2AuthenticationException(oauth2Error, oauth2Error.toString())
        }
        val request = requestEntityConverter.convert(userRequest)
        log.debug("LoadUser Request: {}", request)
        val response = getResponse(userRequest, request)
        log.debug("LoadUser Response: {}", response)
        val userAttributes = response.body
        log.debug("LoadUser UserAttributes: {}", userAttributes)
        val authorities: MutableSet<GrantedAuthority> = LinkedHashSet()
        authorities.add(OAuth2UserAuthority(userAttributes))
        val token = userRequest.accessToken
        for (authority: String in token.scopes) {
            authorities.add(SimpleGrantedAuthority("SCOPE_$authority"))
        }
        return DefaultOAuth2User(authorities, userAttributes, userNameAttributeName)
    }

    private fun getResponse(userRequest: OAuth2UserRequest, request: RequestEntity<*>): ResponseEntity<Map<String, Any>> {
        return try {
            restOperations.exchange(request, PARAMETERIZED_RESPONSE_TYPE)
        } catch (ex: OAuth2AuthorizationException) {
            var oauth2Error = ex.error
            val errorDetails = StringBuilder()
            errorDetails.append("Error details: [")
            errorDetails.append("UserInfo Uri: ")
                    .append(userRequest.clientRegistration.providerDetails.userInfoEndpoint.uri)
            errorDetails.append(", Error Code: ").append(oauth2Error.errorCode)
            if (oauth2Error.description != null) {
                errorDetails.append(", Error Description: ").append(oauth2Error.description)
            }
            errorDetails.append("]")
            oauth2Error = OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE,
                    "An error occurred while attempting to retrieve the UserInfo Resource: $errorDetails",
                    null)
            throw OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex)
        } catch (ex: UnknownContentTypeException) {
            val errorMessage = ("An error occurred while attempting to retrieve the UserInfo Resource from '"
                    + userRequest.clientRegistration.providerDetails.userInfoEndpoint.uri
                    + "': response contains invalid content type '" + ex.contentType.toString() + "'. "
                    + "The UserInfo Response should return a JSON object (content type 'application/json') "
                    + "that contains a collection of name and value pairs of the claims about the authenticated End-User. "
                    + "Please ensure the UserInfo Uri in UserInfoEndpoint for Client Registration '"
                    + userRequest.clientRegistration.registrationId + "' conforms to the UserInfo Endpoint, "
                    + "as defined in OpenID Connect 1.0: 'https://openid.net/specs/openid-connect-core-1_0.html#UserInfo'")
            val oauth2Error = OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE, errorMessage, null)
            throw OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex)
        } catch (ex: RestClientException) {
            val oauth2Error = OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE,
                    "An error occurred while attempting to retrieve the UserInfo Resource: " + ex.message, null)
            throw OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex)
        }
    }

}
