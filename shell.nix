let
  pkgs = import <nixpkgs> { };
in pkgs.mkShell {
  packages = [
    pkgs.jdk17
    pkgs.sbt
    pkgs.cmake
    pkgs.git
    pkgs.scala-cli
  ];
}
