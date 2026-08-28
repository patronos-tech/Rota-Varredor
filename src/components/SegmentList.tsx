import React from 'react';
import { View, Text, StyleSheet, ScrollView } from 'react-native';

interface SegmentInfo {
  id: string;
  streetName: string;
  status: string;
  index: number;
}

interface Props {
  segments: SegmentInfo[];
  currentSegmentId?: string;
}

export const SegmentList: React.FC<Props> = ({ segments, currentSegmentId }) => {
  return (
    <ScrollView style={styles.container}>
      {segments.map((segment) => (
        <View
          key={segment.id}
          style={[
            styles.segmentItem,
            currentSegmentId === segment.id && styles.segmentItemCurrent,
          ]}
        >
          <View style={styles.segmentNumber}>
            <Text style={styles.segmentNumberText}>{segment.index + 1}</Text>
          </View>
          <View style={styles.segmentContent}>
            <Text style={styles.segmentName}>{segment.streetName}</Text>
            <Text style={styles.segmentStatus}>{segment.status}</Text>
          </View>
        </View>
      ))}
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    marginVertical: 8,
  },
  segmentItem: {
    flexDirection: 'row',
    paddingVertical: 12,
    paddingHorizontal: 12,
    backgroundColor: '#f8f9fa',
    borderRadius: 8,
    marginBottom: 8,
    alignItems: 'center',
  },
  segmentItemCurrent: {
    backgroundColor: '#fff3cd',
    borderWidth: 2,
    borderColor: '#f39c12',
  },
  segmentNumber: {
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: '#3498db',
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 12,
  },
  segmentNumberText: {
    fontSize: 14,
    fontWeight: 'bold',
    color: '#fff',
  },
  segmentContent: {
    flex: 1,
  },
  segmentName: {
    fontSize: 16,
    fontWeight: '600',
    color: '#2c3e50',
  },
  segmentStatus: {
    fontSize: 12,
    color: '#7f8c8d',
    marginTop: 2,
  },
});
