import 'package:banjalukawifi/providers/network_provider.dart';
import 'package:banjalukawifi/widgets/network_tile.dart';
import "package:flutter/material.dart";
import 'package:flutter_bloc/flutter_bloc.dart';

class CustomSearchDelegate extends SearchDelegate<String> {
  @override
  List<Widget>? buildActions(BuildContext context) {
    return [
      IconButton(
        onPressed: () {
          query = '';
        },
        icon: Icon(Icons.clear),
      ),
    ];
  }

  // second overwrite to pop out of search menu
  @override
  Widget? buildLeading(BuildContext context) {
    return IconButton(
      onPressed: () {
        close(context, "");
      },
      icon: Icon(Icons.arrow_back),
    );
  }

  @override
  Widget buildResults(BuildContext context) {
    var networks = context.read<NetworkProvider>().searchNetworksByName(query.toLowerCase());

    return ListView.builder(
      itemCount: networks.length,
      itemBuilder: (context, index) {
        return NetworkTile(network: networks[index]);
      },
    );
  }

  @override
  Widget buildSuggestions(BuildContext context) {
    var networks = context.read<NetworkProvider>().searchNetworksByName("");
    return ListView.builder(
      itemCount: networks.length,
      itemBuilder: (context, index) {
        return NetworkTile(network: networks[index]);
      },
    );
  }
}
