import React, { useEffect } from 'react';
import { View, Text, StyleSheet, ScrollView, ActivityIndicator } from 'react-native';
import { useAppStore, useSectorStore } from '@stores';
import { Button } from '@components';
import { generateDemoSector } from '@features/demo/demoDataGenerator';
import { logger } from '@utils/logger';

export function HomeScreen({ navigation }: any) {
  const { sectors, setCurrentSector, setSectors, isLoading, setLoading } = useSectorStore();
  const { setCurrentSector: setAppCurrentSector, config } = useAppStore();

  useEffect(() => {
    initializeApp();
  }, []);

  const initializeApp = async () => {
    try {
      setLoading(true);
      logger.info('Initializing app...');

      // Load demo sector
      const demoSector = generateDemoSector();
      setSectors([demoSector]);

      logger.info('App initialized successfully');
    } catch (error) {
      logger.error('Failed to initialize app', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSectorSelect = (sectorId: string) => {
    const sector = sectors.get(sectorId);
    if (sector) {
      setCurrentSector(sector);
      setAppCurrentSector(sectorId);
      navigation.navigate('Execution', { sectorId });
    }
  };

  if (isLoading) {
    return (
      <View style={styles.centerContainer}>
        <ActivityIndicator size="large" color="#2ecc71" />
        <Text style={styles.loadingText}>Carregando...</Text>
      </View>
    );
  }

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>ROTA VARREDOR</Text>
        <Text style={styles.subtitle}>Sistema de Gestão de Rotas</Text>
      </View>

      <View style={styles.content}>
        <Text style={styles.sectionTitle}>Setores Disponíveis</Text>

        {sectors.size === 0 ? (
          <View style={styles.emptyState}>
            <Text style={styles.emptyStateText}>Nenhum setor disponível</Text>
          </View>
        ) : (
          Array.from(sectors.values()).map((sector) => (
            <View key={sector.id} style={styles.sectorCard}>
              <View style={styles.sectorInfo}>
                <Text style={styles.sectorName}>{sector.name}</Text>
                <Text style={styles.sectorCode}>{sector.code}</Text>
                <Text style={styles.sectorDetails}>
                  {sector.streets.length} ruas • {sector.streets.reduce((acc, s) => acc + s.segments.length, 0)} trechos
                </Text>
              </View>
              <Button
                title="Iniciar"
                variant="primary"
                onPress={() => handleSectorSelect(sector.id)}
              />
            </View>
          ))
        )}
      </View>

      <View style={styles.footer}>
        <Text style={styles.footerText}>Modo: {config.mode}</Text>
        {config.mode === 'demo' && (
          <Text style={styles.demoWarning}>⚠️ Usando dados de demonstração</Text>
        )}
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
    backgroundColor: '#f5f6fa',
  },
  loadingText: {
    marginTop: 12,
    fontSize: 16,
    color: '#2c3e50',
  },
  header: {
    backgroundColor: '#2ecc71',
    paddingTop: 40,
    paddingBottom: 24,
    paddingHorizontal: 16,
  },
  title: {
    fontSize: 28,
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
    paddingVertical: 24,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: '600',
    color: '#2c3e50',
    marginBottom: 16,
  },
  sectorCard: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  sectorInfo: {
    marginBottom: 12,
  },
  sectorName: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#2c3e50',
  },
  sectorCode: {
    fontSize: 12,
    color: '#7f8c8d',
    marginTop: 2,
  },
  sectorDetails: {
    fontSize: 13,
    color: '#95a5a6',
    marginTop: 6,
  },
  emptyState: {
    paddingVertical: 48,
    justifyContent: 'center',
    alignItems: 'center',
  },
  emptyStateText: {
    fontSize: 14,
    color: '#95a5a6',
  },
  footer: {
    paddingHorizontal: 16,
    paddingVertical: 16,
    borderTopWidth: 1,
    borderTopColor: '#ecf0f1',
    backgroundColor: '#fff',
  },
  footerText: {
    fontSize: 12,
    color: '#7f8c8d',
  },
  demoWarning: {
    fontSize: 11,
    color: '#e74c3c',
    marginTop: 4,
  },
});
