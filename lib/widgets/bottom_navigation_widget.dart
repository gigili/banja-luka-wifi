import 'package:banjalukawifi/l10n/l10n.dart';
import "package:flutter/material.dart";

class BottomNavigationWidget extends StatefulWidget {
  final int currentPageIndex;
  final Function(int) onTap;

  const BottomNavigationWidget({
    Key? key,
    required this.currentPageIndex,
    required this.onTap,
  }) : super(key: key);

  @override
  State<BottomNavigationWidget> createState() => _BottomNavigationWidgetState();
}

class _BottomNavigationWidgetState extends State<BottomNavigationWidget> {
  @override
  Widget build(BuildContext context) {
    return BottomNavigationBar(
      type: BottomNavigationBarType.shifting,
      selectedItemColor: Colors.blue[700],
      selectedFontSize: 12,
      unselectedFontSize: 11,
      showUnselectedLabels: false,
      iconSize: 30,
      currentIndex: widget.currentPageIndex,
      items: [
        BottomNavigationBarItem(
          label: context.l10n.lblNetworks,
          icon: Icon(Icons.wifi),
        ),
        BottomNavigationBarItem(
          label: context.l10n.lblAddEditNetwork,
          icon: Icon(Icons.edit),
        ),
        BottomNavigationBarItem(
          label: context.l10n.lblNetworksMap,
          icon: Icon(Icons.map),
        ),
        BottomNavigationBarItem(
          label: context.l10n.lblAppInfo,
          icon: Icon(Icons.info_outline),
        ),
      ],
      onTap: widget.onTap,
    );
  }
}
