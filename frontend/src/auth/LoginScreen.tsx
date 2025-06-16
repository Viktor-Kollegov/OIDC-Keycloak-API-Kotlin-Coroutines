import React, { useState } from 'react';
import { View, Text, Button, StyleSheet, Platform } from 'react-native';
import WebView, { WebViewNavigation } from 'react-native-webview';

// Тип для состояния
interface AuthState {
    isAuthenticating: boolean;
    authResult: { message: string; user: string } | null;
    error: string | null;
}

const LoginScreen: React.FC = () => {
    const [authState, setAuthState] = useState<AuthState>({
        isAuthenticating: false,
        authResult: null,
        error: null,
    });

    // Обработка начала входа
    const startLogin = () => {
        if (Platform.OS === 'web') {
            // Для веба перенаправляем пользователя на страницу входа
            window.location.href = 'http://localhost:8083/api/auth/login';
        } else {
            // Для мобильных устройств показываем WebView
            setAuthState({ ...authState, isAuthenticating: true, error: null });
        }
    };

    // Обработка навигации в WebView (только для мобильных устройств)
    const handleNavigationStateChange = async (navState: WebViewNavigation) => {
        if (navState.url.includes('/api/auth/success')) {
            try {
                const response = await fetch(navState.url, {
                    method: 'GET',
                    headers: { Accept: 'application/json' },
                });
                const data: { message: string; user: string } = await response.json();
                setAuthState({
                    isAuthenticating: false,
                    authResult: data,
                    error: null,
                });
            } catch (err) {
                setAuthState({
                    isAuthenticating: false,
                    authResult: null,
                    error: 'Ошибка при получении данных',
                });
            }
        } else if (navState.url.includes('/error')) {
            setAuthState({
                isAuthenticating: false,
                authResult: null,
                error: 'Ошибка аутентификации',
            });
        }
    };

    // Рендеринг
    if (authState.isAuthenticating && Platform.OS !== 'web') {
        return (
            <WebView
                source={{ uri: 'http://localhost:8083/api/auth/login' }}
                onNavigationStateChange={handleNavigationStateChange}
                style={styles.webview}
            />
        );
    }

    if (authState.authResult) {
        return (
            <View style={styles.container}>
                <Text style={styles.text}>{authState.authResult.message}</Text>
                <Text style={styles.text}>Пользователь: {authState.authResult.user}</Text>
            </View>
        );
    }

    if (authState.error) {
        return (
            <View style={styles.container}>
                <Text style={styles.text}>{authState.error}</Text>
                <Button title="Попробовать снова" onPress={startLogin} />
            </View>
        );
    }

    return (
        <View style={styles.container}>
            <Text style={styles.text}>Добро пожаловать!</Text>
            <Button title="Войти через Keycloak" onPress={startLogin} />
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
    },
    webview: {
        flex: 1,
    },
    text: {
        fontSize: 18,
        marginBottom: 10,
    },
});

export default LoginScreen;