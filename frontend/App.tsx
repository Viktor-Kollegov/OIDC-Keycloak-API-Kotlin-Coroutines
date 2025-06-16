import React from 'react';
import { ScrollView } from 'react-native';
import LoginScreen from './src/auth/LoginScreen'; // Укажи правильный путь к файлу

export default function App() {
    return (
        <ScrollView contentContainerStyle={{ flexGrow: 1, justifyContent: 'center', padding: 20 }}>
            <LoginScreen />
        </ScrollView>
    );
}