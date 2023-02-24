import 'package:banjalukawifi/cubit/network/network_cubit.dart';
import 'package:banjalukawifi/models/network_model.dart';
import 'package:banjalukawifi/widgets/network_tile.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

class NetworkScreen extends StatefulWidget {
  const NetworkScreen({Key? key}) : super(key: key);

  @override
  State<NetworkScreen> createState() => _NetworkScreenState();
}

class _NetworkScreenState extends State<NetworkScreen> {
  @override
  Widget build(BuildContext context) {
    return BlocConsumer<NetworkCubit, NetworkState>(
      listener: (BuildContext context, NetworkState state) {
        if (state is NetworkError) {
          /*ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text(context.l10n.)),
          );*/
          //TODO: Show proper error message
        }
      },
      builder: (BuildContext context, NetworkState state) {
        if (state is NetworkLoading || state is NetworkInitial) {
          return const Center(child: CircularProgressIndicator());
        }

        if (state is NetworkError) return Container();

        return Container(
          margin: EdgeInsets.only(top: 10),
          child: RefreshIndicator(
            onRefresh: () async {
              context.read<NetworkCubit>().fetchNetworks();
            },
            child: ListView.separated(
              itemCount: (state as NetworkLoaded).networks.length,
              separatorBuilder: (BuildContext ctx, int index) => const Divider(thickness: 1),
              itemBuilder: (BuildContext ctx, int index) {
                NetworkModel network = state.networks[index];
                return NetworkTile(network: network);
              },
            ),
          ),
        );
      },
    );
  }
}
