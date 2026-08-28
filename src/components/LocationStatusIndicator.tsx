import React from 'react';
import { View, Text, StyleSheet, ActivityIndicator } from 'react-native';

interface Props {
  status: 'correct' | 'approaching' | 'incorrect';
  distance?: number;
  street?: string;
}

export const LocationStatusIndicator: React.FC<Props> = ({
  status,
  distance,
  street,
}) => {
  const getStatusColor = () => {
    switch (status) {
      case 'correct':
        return '#2ecc71';
      case 'approaching':
        return '#f39c12';
      case 'incorrect':
        return '#e74c3c';
      default:
        return '#95a5a6';
    }
  };

  const getStatusText = () => {
    switch (status) {
      case 'correct':
        return 'RUA CORRETA';
      case 'approaching':
        return 'APROXIMANDO-SE';
      case 'incorrect':
        return 'FORA DA ROTA';
      default:
        return 'AGUARDANDO GPS';
    }
  };

  return (
    <View style={[styles.container, { backgroundColor: getStatusColor() }]}>
      <Text style={styles.statusText}>{getStatusText()}</Text>
      {street && <Text style={styles.streetText}>{street}</Text>}
      {distance !== undefined && <Text style={styles.distanceText}>{Math.round(distance)} m</Text>}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    paddingVertical: 12,
    paddingHorizontal: 16,
    borderRadius: 8,
    marginVertical: 8,
  },
  statusText: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#fff',
  },
  streetText: {
    fontSize: 14,
    color: '#fff',
    marginTop: 4,
  },
  distanceText: {
    fontSize: 12,
    color: '#fff',
    marginTop: 2,
  },
});
