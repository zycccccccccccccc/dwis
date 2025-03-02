    SELECT
    wheel_record.wheel_id AS id,
    ladle_record.ladle_record_key AS ladle_record_key,
    wheel_record.wheel_serial,
    wheel_record.shelf_number,
    contract_record.contract_no,
    wheel_record.design,
    wheel_record.wheel_w,
    train_no.shipped_date,
    train_no.shipped_no,
    wheel_record.tape_size,
    customer.customer_name,
    train_no.train_no,
    wheel_record.balance_s,
    wheel_record.brinnel_reading,
    design.drawing_no,
    design.approbation_no,
    design.spec,
    design.transfer_record_no,
    design.steel_class,
    chemistry_detail.C AS C,
    chemistry_detail.Mn AS Mn,
    chemistry_detail.P AS P,
    chemistry_detail.S AS S,
    chemistry_detail.Si AS Si,
    chemistry_detail.Cr AS Cr,
    chemistry_detail.Ni AS Ni,
    chemistry_detail.Mo AS Mo,
    chemistry_detail.Cu AS Cu,
    chemistry_detail.Nb AS Nb,
    chemistry_detail.V  AS V,
    chemistry_detail.Ti AS Ti,
    chemistry_detail.Al AS Al,
    chemistry_detail.B AS H,
    wheel_record.check_code,
    pour_record.batch_no
    FROM customer
    INNER JOIN train_no ON customer.customer_id = train_no.customer_id
    INNER JOIN contract_record ON contract_record.id = train_no.c_id
    INNER JOIN wheel_record ON train_no.shipped_no = wheel_record.shipped_no
    INNER JOIN design ON design.design = wheel_record.design
    INNER JOIN chemistry_detail	ON chemistry_detail.ladle_id = wheel_record.ladle_id
    INNER JOIN pour_record ON wheel_record.wheel_serial = pour_record.wheel_serial
    INNER JOIN ladle_record ON wheel_record.ladle_id = ladle_record.id
    WHERE train_no.shipped_no = :shippedNo
    ORDER BY wheel_record.wheel_serial
