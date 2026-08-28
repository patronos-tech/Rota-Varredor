import React from 'react';
import { SafeAreaView, StatusBar } from 'react-native';
import { RootNavigator } from './RootNavigator';

export default function App() {
  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: '#fff' }}>
      <StatusBar barStyle="dark-content" backgroundColor="#fff" />
      <RootNavigator />
    </SafeAreaView>
  );
}
