import 'package:banjalukawifi/l10n/l10n.dart';
import 'package:banjalukawifi/models/network_model.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class NetworkTile extends StatelessWidget {
  final NetworkModel network;

  const NetworkTile({super.key, required this.network});

  @override
  Widget build(BuildContext context) {
    return ListTile(
      key: Key(network.id ?? ""),
      title: Text(network.name),
      subtitle: Text(network.password),
      onTap: () {
        Clipboard.setData(ClipboardData(text: network.password)).then((value) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text(context.l10n.msgPasswordCopiedToClipboard)),
          );
        });
      },
    );
  }
}
