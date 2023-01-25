import "dart:developer";

import 'package:appwrite/appwrite.dart';
import 'package:appwrite/models.dart';
import 'package:banjalukawifi/models/network_model.dart';
import 'package:banjalukawifi/utility/constants.dart';
import 'package:flutter/foundation.dart';

class ApiProvider {
  ApiProvider();

  Future<List<NetworkModel>> fetchNetworks() async {
    Databases db = databases;
    List<NetworkModel> networks = [];
    try {
      DocumentList result = await db.listDocuments(
        databaseId: databaseID,
        collectionId: collectionID,
        queries: [
          Query.equal('approved', true).toString(),
          Query.orderDesc("lastUpdate"),
          Query.limit(100),
        ],
      );

      networks.addAll(result.documents.map((e) => NetworkModel.fromDocument(e.$id, e.data, e.$updatedAt)).toList());

      var page = 0;
      while (networks.length < result.total) {
        var res = await fetchNetworksPagination(page);
        page += 100;
        res.removeWhere((element) => networks.contains(element));
        networks.addAll(res);
      }

      return networks;
    } catch (e) {
      if (kDebugMode) {
        log(e.toString());
      }
    }

    return [];
  }

  Future<List<NetworkModel>> fetchNetworksPagination(int page) async {
    Databases db = databases;

    try {
      DocumentList result = await db.listDocuments(
        databaseId: databaseID,
        collectionId: collectionID,
        queries: [
          Query.equal('approved', true).toString(),
          Query.orderDesc("lastUpdate"),
          Query.limit(100),
          Query.offset(page),
        ],
      );

      return result.documents.map((e) => NetworkModel.fromDocument(e.$id, e.data, e.$updatedAt)).toList();
    } catch (e) {
      if (kDebugMode) {
        log(e.toString());
      }
    }

    return [];
  }

  Future<void> submitBug({
    required String name,
    required String email,
    required String description,
  }) async {}

  Future<void> submitNetwork({required NetworkModel network}) async {
    Databases db = databases;
    db.createDocument(
      databaseId: databaseID,
      collectionId: collectionID,
      documentId: "unique()",
      data: network.toMap(),
    );
  }
}
