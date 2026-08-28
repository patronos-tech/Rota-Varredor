import React, { useEffect, useState } from 'react';
import { View, Text, StyleSheet, ScrollView, ActivityIndicator } from 'react-native';
import { Button } from '@components';
import type { Execution } from '@types/domain';

export function HistoryScreen({ route, navigation }: any) {
  const { executionId } = route.params;
  const [execution, setExecution] = useState<Execution | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    loadExecution();
  }, [executionId]);

  const loadExecution = async () => {
    try {
      setIsLoading(true);
      // In real app, fetch from API
      // For now, simulate with mock data
      setIsLoading(false);
    } catch (error) {
      setIsLoading(false);
    }
  };

  if (isLoading) {
    return (
      <View style={styles.centerContainer}>
        <ActivityIndicator size="large" color="#2ecc71" />
      </View>
    );
  }

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Execução Finalizada</Text>
        <Text style={styles.subtitle}>ID: {executionId}</Text>
      </View>

      <View style={styles.content}>
        <View style={styles.card}>
          <Text style={styles.cardTitle}>Resumo da Execução</Text>
          <View style={styles.row}>
            <Text style={styles.label}>Status:</Text>
            <Text style={styles.value}>Concluído ✓</Text>
          </View>
          <View style={styles.row}>
            <Text style={styles.label}>Data:</Text>
            <Text style={styles.value}>{new Date().toLocaleDateString('pt-BR')}</Text>
          </View>
        </View>

        <View style={styles.card}>
          <Text style={styles.cardTitle}>Métricas</Text>
          <View style={styles.row}>
            <Text style={styles.label}>Distância Total:</Text>
            <Text style={styles.value}>2.5 km</Text>
          </View>
          <View style={styles.row}>
            <Text style={styles.label}>Tempo Total:</Text>
            <Text style={styles.value}>1h 30min</Text>
          </View>
          <View style={styles.row}>
            <Text style={styles.label}>Velocidade Média:</Text>
            <Text style={styles.value}>1.67 km/h</Text>
          </View>
        </View>

        <View style={styles.actions}>
          <Button
            title="Voltar ao Início"
            variant="primary"
            onPress={() => navigation.navigate('Home')}
          />
          <Button
            title="Iniciar Novo Setor"
            variant="secondary"
            onPress={() => navigation.navigate('Home')}
          />
        </View>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f6fa',
  },
  centerContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  header: {
    backgroundColor: '#2ecc71',
    paddingTop: 16,
    paddingBottom: 16,
    paddingHorizontal: 16,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#fff',
  },
  subtitle: {
    fontSize: 14,
    color: '#ecf0f1',
    marginTop: 4,
  },
  content: {
    paddingHorizontal: 16,
    paddingVertical: 16,
  },
  card: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 16,
    marginBottom: 16,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  cardTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#2c3e50',
    marginBottom: 12,
  },
  row: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingVertical: 8,
    borderBottomWidth: 1,
    borderBottomColor: '#ecf0f1',
  },
  label: {
    fontSize: 14,
    color: '#7f8c8d',
    fontWeight: '500',
  },
  value: {
    fontSize: 14,
    color: '#2c3e50',
    fontWeight: '600',
  },
  actions: {
    gap: 12,
    marginTop: 24,
  },
});
