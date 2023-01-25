import 'package:banjalukawifi/models/network_model.dart';
import 'package:hive_flutter/hive_flutter.dart';

class BoxProvider {
  final Box<NetworkModel> _box;

  BoxProvider(this._box);

  List<NetworkModel> fetchNetworks() {
    return _box.values.toList();
  }

  void addNetworks(List<NetworkModel> networks) {
    for (final network in networks) {
      _box.put(network.id, network);
    }
  }

  NetworkModel? findNetworkByName(String networkName) {
    try {
      return _box.values.firstWhere((element) => element.name == networkName);
    } catch (_) {
      return null;
    }
  }

  List<NetworkModel> searchNetworksByName(String query) {
    try {
      return _box.values.where((element) => element.name.toLowerCase().contains(query)).toList();
    } catch (_) {
      return [];
    }
  }
}
