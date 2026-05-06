
#import "@preview/fletcher:0.5.8" as fletcher: diagram, node, edge

#import fletcher.shapes: brace, diamond

#set page(width: 180mm, height: 60mm)

#let debug = false
#set align(center)

#let rect_stroke = 1pt
#let corner_radius = 5pt
#let def_outset = 5pt

#let col_export = blue
#let col_import = green
#let col_cc = orange 

#let edge_export = stroke(thickness: 1pt, paint: col_export)
#let edge_neutral = stroke(thickness: 1pt, paint: black)
#let edge_import = stroke(thickness: 1pt, paint: col_import)

#diagram(
  debug: debug,
node((2, 0),"Contract-LIB", stroke: rect_stroke, corner-radius: corner_radius, name: <cc>, outset: def_outset),
node((0, 3),"KeY", stroke: rect_stroke, corner-radius: rect_stroke, name: <key>, outset: def_outset),
//node((1, 3),"KeY + Universe Types", stroke: rect_stroke, corner-radius: rect_stroke, name: <key_ut>, outset: def_outset),
node((2, 3),"Verifast", stroke: rect_stroke, corner-radius: rect_stroke, name: <vf>, outset: def_outset),
node((5, 3),"Other Verifaction Tools", stroke: rect_stroke, corner-radius: rect_stroke, name: <open>, outset: def_outset),
edge(<cc>, "->", <key>, bend: -23deg, label: [#set text(col_export); `key-provider` \ `key-applicant`], stroke: edge_export),
//edge(<cc>, "->", <key_ut>, bend: -23deg, stroke: red, `t`),
edge(<cc>, "->", <vf>, label: [#set text(blue); `verifast-provider` \ `verifast-applicant`], label-side: left, stroke: edge_export, label-pos: 0.7),
edge(<cc>, "<->", <open>, bend: 23deg,label: [`Future Adapters`], stroke: edge_neutral),
edge(<cc>, "<-", <key>, bend: 23deg,label: [#set text(col_import); `key-import`], label-side: center, stroke: edge_import),
node(enclose: (<cc>, <open>), shape: brace.with(dir: right, label: text(orange)[#raw("contract-chameleon")], fill: orange, length: 100% - 6em, sep: -4em)),
)
