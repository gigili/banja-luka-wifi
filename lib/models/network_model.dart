import 'dart:convert';

import 'package:hive/hive.dart';

part 'network_model.g.dart';

@HiveType(typeId: 1)
class NetworkModel extends HiveObject {
  @HiveField(0)
  final String? id;

  @HiveField(1)
  final String name;

  @HiveField(2)
  final String password;

  @HiveField(3)
  final String? address;

  @HiveField(4)
  final double? geoLat;

  @HiveField(5)
  final double? geoLong;

  @HiveField(6)
  late String? lastUpdate;

  NetworkModel({
    required this.id,
    required this.name,
    required this.password,
    this.address,
    this.geoLat,
    this.geoLong,
    this.lastUpdate,
  });

  NetworkModel.fromDocument(String this.id, Map<String, dynamic> data, [this.lastUpdate])
      : name = data['name'].toString(),
        password = data['password'].toString(),
        address = data['address'].toString(),
        geoLat = double.parse(data['geoLat'].toString()),
        geoLong = double.parse(data['geoLong'].toString());

  String toJSON() {
    final obj = {
      'id': id,
      'name': name,
      'address': address,
      'password': password,
      'geoLat': geoLat,
      'geoLong': geoLong,
      'lastUpdate': lastUpdate,
    };

    return jsonEncode(obj);
  }

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'name': name,
      'address': address,
      'password': password,
      'geoLat': geoLat,
      'geoLong': geoLong,
      'lastUpdate': lastUpdate,
    };
  }

  @override
  bool operator ==(Object other) {
    return other is NetworkModel && (id == other.id);
  }

  @override
  int get hashCode => int.parse(((geoLat ?? 0) + (geoLong ?? 1)).toString());
}
