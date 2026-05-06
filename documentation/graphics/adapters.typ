#import "@preview/fletcher:0.5.8" as fletcher: diagram, node, edge

#import fletcher.shapes: brace, diamond

#set page(width: 210mm, height: 110mm)

#let debug = false
#set align(center + horizon)

#let rect_stroke = 1pt
#let rect_stroke_thin = 0.1pt
#let corner_radius = 5pt
#let def_outset = 5pt

#let col_export = blue
#let col_import = green
#let col_checker = purple 
#let col_cc = orange 
#let col_provider = blue.darken(50%) 
#let col_applicant =blue.lighten(50%)

#let edge_export = stroke(thickness: 1pt, paint: col_export)
#let edge_neutral = stroke(thickness: 1pt, paint: black)
#let edge_import = stroke(thickness: 1pt, paint: col_import)

#diagram(
  debug: debug,
node(enclose: ((-1.5, 0), (5, 0)),text(col_cc)[contract-chameleon], corner-radius: corner_radius, name: <cc>, stroke: (thickness: rect_stroke, paint: col_cc)),

node(enclose: (<adapter>, <verifast-applicant>, <applicant>, <strict-encapsulation>, <cc>), stroke: (thickness: rect_stroke, paint: col_cc), corner-radius: corner_radius, name: <cc>),

node((2, 1),"Adapter", stroke: rect_stroke, corner-radius: corner_radius, name: <adapter>, outset: def_outset),
node((1, 2),"Translation Adapter", stroke: rect_stroke, corner-radius: corner_radius, name: <translation>, outset: def_outset),
node((3, 2),text(col_checker)[Checker Adapter], stroke: (thickness: rect_stroke, paint: col_checker), corner-radius: corner_radius, name: <checker>, outset: def_outset),
node((0, 3),text(col_export)[Export Adapter], stroke: (thickness: rect_stroke, paint: col_export), corner-radius: corner_radius, name: <export>, outset: def_outset),
node((2, 3),text(col_import)[Import Adapter], stroke: (thickness: rect_stroke, paint: col_import), corner-radius: corner_radius, name: <import>, outset: def_outset),
node((-0.5, 4),text(col_applicant)[Applicant Adapter], stroke: (thickness: rect_stroke, paint: col_applicant), corner-radius: corner_radius, name: <applicant>, outset: def_outset),
node((0.5, 4),text(col_provider)[Provider Adapter], stroke: (thickness: rect_stroke, paint: col_provider), corner-radius: corner_radius, name: <provider>, outset: def_outset),

node((rel: (0em, -2em), to: <applicant>),"key-applicant", stroke: rect_stroke_thin, corner-radius: corner_radius, name: <key-applicant>, outset: def_outset),
node((rel: (0em, -4em), to: <applicant>),"verifast-applicant", stroke: rect_stroke_thin, corner-radius: corner_radius, name: <verifast-applicant>, outset: def_outset),
node((rel: (0em, -2em), to: <provider>),"key-provider", stroke: rect_stroke_thin, corner-radius: corner_radius, name: <key-provider>, outset: def_outset),
node((rel: (0em, -4em), to: <provider>),"verifast-provider", stroke: rect_stroke_thin, corner-radius: corner_radius, name: <verifast-provider>, outset: def_outset),

node((rel: (0em, -2em), to: <import>),"key-import", stroke: rect_stroke_thin, corner-radius: corner_radius, name: <key-import>, outset: def_outset),
node((rel: (0em, -2em), to: <checker>),"strict-encapsulation", stroke: rect_stroke_thin, corner-radius: corner_radius, name: <strict-encapsulation>, outset: def_outset),

edge(<adapter>, (2, 1.5), (1, 1.5), <translation>, "->", shift: (8pt, 0pt)),
edge(<adapter>, (2, 1.5), (3, 1.5), <checker>, "->", shift: (-8pt, 0pt)),

edge(<translation>,  (1, 2.5), (0, 2.5), <export>, "->", shift: (8pt, 0pt)),
edge(<translation>, (1, 2.5), (2, 2.5), <import>, "->", shift: (-8pt, 0pt)),


edge(<export>,  (0, 3.5), (-0.5, 3.5), <applicant>, "->", shift: (8pt, 0pt)),
edge(<export>,   (0, 3.5), (0.5, 3.5), <provider>, "->", shift: (-8pt, 0pt)),
//node((0, 3),"KeY", stroke: rect_stroke, corner-radius: rect_stroke, name: <key>, outset: def_outset),
//node((1, 3),"KeY + Universe Types", stroke: rect_stroke, corner-radius: rect_stroke, name: <key_ut>, outset: def_outset),
//node((2, 3),"Verifast", stroke: rect_stroke, corner-radius: rect_stroke, name: <vf>, outset: def_outset),
//node((5, 3),"Other Verifaction Tools", stroke: rect_stroke, corner-radius: rect_stroke, name: <open>, outset: def_outset),
//edge(<cc>, "->", <key>, bend: -23deg, label: [#set text(col_export); `key-provider` \ `key-applicant`], stroke: edge_export),
//edge(<cc>, "->", <key_ut>, bend: -23deg, stroke: red, `t`),
//edge(<cc>, "->", <vf>, label: [#set text(blue); `verifast-provider` \ `verifast-applicant`], label-side: left, stroke: edge_export, label-pos: 0.7),
//edge(<cc>, "<->", <open>, bend: 23deg,label: [`Future Adapters`], stroke: edge_neutral),
//edge(<cc>, "<-", <key>, bend: 23deg,label: [#set text(col_import); `key-import`], label-side: center, stroke: edge_import),
)
